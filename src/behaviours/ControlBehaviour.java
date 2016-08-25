package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.ControlData;
import basicData.ControlFlexibilityData;
import basicData.FlexibilityData;
import basicData.ResultPowerPrice;
import basicData.TimePowerPrice;
import database.DbControlArrivalData;
import database.DbControlData;
import database.DbTimePowerPrice;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class ControlBehaviour extends OneShotBehaviour {

	ArrayList<TimePowerPrice> prices;
	ControlFlexibilityData batteryData;
	ControlFlexibilityData derData;
	ControlFlexibilityData loadData;
	
	double derEnergyRequest = 0;
	double loadEnergyRequest = 0;
	double batteryEnergyRequest = 0;
	double costKwh = 0;
	double gridEnergyRequest = 0;
	
	ACLMessage msg;
	FlexibilityData msgData;
	public ControlBehaviour(ACLMessage msg) {
		this.msg = msg;
		try {
			msgData = (FlexibilityData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void action() {
		/**
		 * waits for messages to arrive from the 3 aggregators and make the choice 
		 * In version 2.0 I can implement a "persistent delivery of ACL messages" 
		 * 
		 *
		 * Send the message with the result that is a single vector made by 3 values: 
		 * PERIOD (sec) - POWER (kw) - COST (€) -> Result.java
		 */
		
		
		// DEVO RICEVERE TUTTI E 3 I MESSAGGI, SE NON LI RICEVO ALLORA DEVO FARE QUALCOSA PER RECUPERARLI
		// SI MA POI SI VEDE, I won't ignore :)
		
		ControlFlexibilityData controlArrivalData = new ControlFlexibilityData(this.myAgent.getName(), msgData);
		controlArrivalData.setIdAgent(this.myAgent.getName());
		new DbControlArrivalData().addControlArrivalData(controlArrivalData);

		int messagesReceived = new DbControlArrivalData().countMessagesReceived(this.myAgent.getName());
		System.out.println("ControlAgent messagesReceived: "+messagesReceived);
		if (messagesReceived == 3)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * think and tell them what to do 
			 */
			prices = new DbTimePowerPrice().getDailyTimePowerPrice(controlArrivalData.getDatetime());
			batteryData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "battery");
			derData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "der");
			loadData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "load");
			
			double meanPrice = calculateMeanPrice(prices);
			System.out.println("meanPrice :"+meanPrice);
			
			if(meanPrice > prices.get(0).getEnergyPrice() + 0.2*meanPrice)
			{
				System.out.println("Now buy energy from the grid - no use battery, maybe charge it");
				/**
				 * Now buy energy from the grid - no use battery, maybe charge it
				 */
				derEnergyRequest = derData.getDesideredChoice();
				loadEnergyRequest = loadData.getUpperLimit();			
				System.out.println("\n1 - gridEnergyRequest: "+gridEnergyRequest);
				System.out.println("\n1 - derEnergyRequest: "+derEnergyRequest);
				System.out.println("\n1 - batteryEnergyRequest: "+batteryEnergyRequest);
				System.out.println("\n1 - loadEnergyRequest: "+loadEnergyRequest);
				//se la produzione è maggiore del carico, carico le batterie
				if (derEnergyRequest > loadEnergyRequest)
				{
					System.out.println("1La produzione in eccedenza la metto in batteria finché possibile");
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getUpperLimit()
							? batteryData.getUpperLimit() : (derEnergyRequest - loadEnergyRequest);
				}
				
				//se sono molto scariche e se conviene assai, carico le batterie, altrimenti no
				if(batteryData.getDesideredChoice() > 0 &&
						meanPrice > prices.get(0).getEnergyPrice() + 0.5*meanPrice)
				{
					System.out.println("I charge batteries as much as they want to");
					batteryEnergyRequest = batteryData.getDesideredChoice() > 0 ? 
							batteryData.getDesideredChoice() : batteryEnergyRequest;
				}
			}
			else if(meanPrice < prices.get(0).getEnergyPrice() - 0.2*meanPrice)
			{
				System.out.println("Now I buy less energy and use batteries and DER");
				/**
				 * Now use battery
				 */
				derEnergyRequest = derData.getUpperLimit();
				loadEnergyRequest = loadData.getLowerLimit();
				if(batteryData.getCostKwh() > prices.get(0).getEnergyPrice() && 
						derData.getCostKwh() > prices.get(0).getEnergyPrice())
				{
					System.out.println("I will not use battery or der");
					derEnergyRequest = -derData.getDesideredChoice();
					batteryEnergyRequest = 0;
				}
				//se la produzione è maggiore del carico, carico le batterie
				if (derEnergyRequest > loadEnergyRequest)
				{
					System.out.println("2La produzione in eccedenza la metto in batteria finché possibile");
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getUpperLimit()
							? batteryData.getUpperLimit() : (derEnergyRequest - loadEnergyRequest);
				}
			}
			else
			{
				/**
				 * Now the price is near the mean value, 
				 * I try to use batteries and more DER energy if it's more convenient
				 * I try to shift loads if it's convenient
				 */
				derEnergyRequest = derData.getDesideredChoice();
				loadEnergyRequest = loadData.getDesideredChoice();
				
				if(loadData.getCostKwh() < prices.get(0).getEnergyPrice())
				{
					loadEnergyRequest = loadData.getUpperLimit();
				}
				if(batteryData.getCostKwh() < prices.get(0).getEnergyPrice())
				{
					batteryEnergyRequest = batteryData.getDesideredChoice() < 0 
							? batteryData.getDesideredChoice() : 0;
				}
				if(derData.getCostKwh() < prices.get(0).getEnergyPrice())
				{
					derEnergyRequest = derData.getUpperLimit();
				}
				
				//se la produzione è maggiore del carico, carico le batterie
				if (derEnergyRequest > loadEnergyRequest)
				{
					System.out.println("3La produzione in eccedenza la metto in batteria finché possibile");
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getLowerLimit()
							? batteryData.getLowerLimit() : (derEnergyRequest - loadEnergyRequest);
					batteryEnergyRequest = GeneralData.round(batteryEnergyRequest, 2);
				}
			}
			
			gridEnergyRequest = GeneralData.round(loadEnergyRequest+batteryEnergyRequest-derEnergyRequest, 2);

			peakShaving();
			
			System.out.println("\ngridEnergyRequest: "+gridEnergyRequest);
			System.out.println("\n derEnergyRequest: "+-derEnergyRequest);
			System.out.println("\n batteryEnergyRequest: "+batteryEnergyRequest);
			System.out.println("\n loadEnergyRequest: "+loadEnergyRequest);
			
			costKwh = prices.get(0).getEnergyPrice();

			ResultPowerPrice derResult = new ResultPowerPrice(msgData.getDatetime(), derEnergyRequest, costKwh);
			ResultPowerPrice batteryResult = new ResultPowerPrice(msgData.getDatetime(), batteryEnergyRequest, costKwh);
			ResultPowerPrice loadResult = new ResultPowerPrice(msgData.getDatetime(), loadEnergyRequest, costKwh);
			
			
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "BatteryAggregatorAgent",
					"result", batteryResult);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
					"result", derResult);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
					"result", loadResult);
			
			System.out.println("batteryEnergyRequest 1: "+batteryEnergyRequest);
			
			ControlData newControlData = new ControlData(this.myAgent.getName(), this.myAgent.getHap(),
					msgData.getDatetime(), -derEnergyRequest, batteryEnergyRequest, loadEnergyRequest, 
					gridEnergyRequest, costKwh, 0);
			new DbControlData().addControlData(newControlData);
		}
	}
	
	/**
	 * Checks if the list of what the agents decided to does not overcome the limit given as input 
	 * @return
	 */
	private void peakShaving()
	{
		if(gridEnergyRequest > prices.get(0).getThreshold())
		{
			derEnergyRequest = derData.getUpperLimit();
			gridEnergyRequest = GeneralData.round(batteryEnergyRequest+loadEnergyRequest-derEnergyRequest, 2);
			if(gridEnergyRequest > prices.get(0).getThreshold())
			{
				batteryEnergyRequest = batteryData.getUpperLimit();
			}
			gridEnergyRequest = GeneralData.round(batteryEnergyRequest+loadEnergyRequest-derEnergyRequest, 2);
			if(gridEnergyRequest > prices.get(0).getThreshold())
			{
				loadEnergyRequest = loadData.getLowerLimit();
			}
		}
	}
	
	private double calculateMeanPrice (ArrayList<TimePowerPrice> prices)
	{
		float sum=0;
		for(int i=0; i<prices.size(); i++)
		{
			sum += prices.get(i).getEnergyPrice();
		}
		return GeneralData.round(sum/prices.size(), 4);
	}
	
	/**
	 * 
	 * @return 1 if currentEnergyPrice is in the 30% higher price
	 * @return -1 if currentEnergyPrice is in the 30% lower price
	 * @return 0 otherwise
	 */
	private int checkPercentile(double currentEnergyPrice)
	{
		int counter = 0;
		ArrayList<TimePowerPrice> lastWeekPrices = 
		for(int i=0; i < prices.size(); i++)
		{
			if(currentEnergyPrice > prices.get(i).getEnergyPrice())
				counter++;
		}
		double percentile = counter/prices.size();
		if(percentile > 0.7) 
			return 1;
		else if(percentile < 0.3)
			return -1;
		else 
			return 0;
	}

}
