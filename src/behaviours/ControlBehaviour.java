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
		 * PERIOD (sec) - POWER (kw) - COST (�) -> Result.java
		 */
		
		
		// To-do DEVO RICEVERE TUTTI E 3 I MESSAGGI, SE NON LI RICEVO ALLORA DEVO FARE QUALCOSA PER RECUPERARLI
		// SI MA POI SI VEDE, I won't ignore :)
		System.out.println("sono nel Control Agent, messaggio da: "+msgData.getType());
		ControlFlexibilityData controlArrivalData = new ControlFlexibilityData(this.myAgent.getName(), msgData);
		new DbControlArrivalData().addControlArrivalData(controlArrivalData);

		int messagesReceived = new DbControlArrivalData().countMessagesReceived(this.myAgent.getName(), msgData.getDatetime());
		//System.out.println("ControlAgent messagesReceived: "+messagesReceived);
		if (messagesReceived == 3)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * think and tell them what to do 
			 */
			prices = new DbTimePowerPrice().getDailyTimePowerPrice(controlArrivalData.getDatetime());
			batteryData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "battery", msgData.getDatetime());
			derData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "der", msgData.getDatetime());
			loadData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "load", msgData.getDatetime());
			
			System.out.println("\n\nbatt - min: "+batteryData.getLowerLimit()+" max: "+batteryData.getUpperLimit()+" desidered: "+batteryData.getDesideredChoice());
			System.out.println("der - min: "+derData.getLowerLimit()+" max: "+derData.getUpperLimit()+" desidered: "+derData.getDesideredChoice());
			System.out.println("load - min: "+loadData.getLowerLimit()+" max: "+loadData.getUpperLimit()+" desidered: "+loadData.getDesideredChoice()+"\n\n");
			
			if(checkPercentile(prices.get(0).getEnergyPrice()) < 0.3) //energy cost low
			{
				System.out.println("Now buy energy from the grid - no use battery, maybe charge it");
				/**
				 * Now buy energy from the grid - no use battery, maybe charge it
				 */
				derEnergyRequest = derData.getDesideredChoice();
				loadEnergyRequest = loadData.getDesideredChoice();			
				
				if(derData.getCostKwh() < prices.get(0).getEnergyPrice()) //se conviene produco a palla per�
				{
					derEnergyRequest = derData.getUpperLimit();
				}
				
				//se la produzione � maggiore del carico, carico le batterie
				if (derEnergyRequest > loadData.getDesideredChoice())
				{
					//System.out.println("1La produzione in eccedenza la metto in batteria finch� possibile");
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getLowerLimit()
							? batteryData.getLowerLimit() : (derEnergyRequest - loadEnergyRequest);
				}
				
				//TO-DO se sono molto scariche e ->se conviene assai<-, carico le batterie, altrimenti no
				if(batteryData.getDesideredChoice() > 0)
				{
					batteryEnergyRequest = batteryData.getDesideredChoice() > 0 ? 
							batteryData.getDesideredChoice() : batteryEnergyRequest;
				}
			}
			else if(checkPercentile(prices.get(0).getEnergyPrice()) > 0.7) //energy cost high
			{
				System.out.println("Now I buy less energy and use batteries and DER");
				/**
				 * Now use battery
				 */
				derEnergyRequest = derData.getUpperLimit();
				loadEnergyRequest = loadData.getDesideredChoice();
				
				/*if(batteryData.getCostKwh() > prices.get(0).getEnergyPrice() &&
						derData.getCostKwh() > prices.get(0).getEnergyPrice())
				{
					//I will not use battery or der
					derEnergyRequest = derData.getDesideredChoice();
					batteryEnergyRequest = 0;
				}*/
				//se la produzione � maggiore del carico, carico le batterie
				if (derEnergyRequest > loadEnergyRequest)
				{
					//System.out.println("2La produzione in eccedenza la metto in batteria finch� possibile");
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getLowerLimit()
							? batteryData.getLowerLimit() : (derEnergyRequest - loadEnergyRequest);
				}
				//uso la batteria
				else if(derEnergyRequest < loadEnergyRequest)
				{
					//System.out.println("CONTROLLA: loadEnergyRequest - derEnergyRequest = "+(loadEnergyRequest-derEnergyRequest));
					batteryEnergyRequest = (loadEnergyRequest - derEnergyRequest) > -batteryData.getUpperLimit()
							? batteryData.getUpperLimit() : -(loadEnergyRequest - derEnergyRequest);
					//System.out.println(batteryEnergyRequest);		
				}
			}
			else //energy cost in the mean
			{
				/**
				 * Now the price is near the mean value, 
				 * I try to use batteries and more DER energy if it's more convenient
				 * I try to shift loads if it's convenient
				 */
				derEnergyRequest = derData.getDesideredChoice();
				loadEnergyRequest = loadData.getDesideredChoice();
				
				/*if(loadData.getCostKwh() < prices.get(0).getEnergyPrice())
				{
					loadEnergyRequest = loadData.getUpperLimit();
				}*/
				
				if(derData.getCostKwh() < prices.get(0).getEnergyPrice())
				{
					derEnergyRequest = derData.getUpperLimit();
				}
				
				//se la produzione � maggiore del carico, carico le batterie
				if (derEnergyRequest > loadEnergyRequest)
				{
					batteryEnergyRequest = (derEnergyRequest - loadEnergyRequest) > batteryData.getLowerLimit()
							? batteryData.getLowerLimit() : (derEnergyRequest - loadEnergyRequest);
				}
				else if(batteryData.getCostKwh() < prices.get(0).getEnergyPrice() 
						&& batteryData.getDesideredChoice() < 0) //se conviene, uso le batterie ma non al massimo
				{
					batteryEnergyRequest = (loadEnergyRequest - derEnergyRequest) > -batteryData.getDesideredChoice()
							? batteryData.getDesideredChoice() : -(loadEnergyRequest - derEnergyRequest);
				}
			}
			
			gridEnergyRequest = GeneralData.round(loadEnergyRequest+batteryEnergyRequest-derEnergyRequest, 2);
			peakShaving();
			
			derEnergyRequest = GeneralData.round(derEnergyRequest, 2);
			loadEnergyRequest = GeneralData.round(loadEnergyRequest, 2);
			batteryEnergyRequest = GeneralData.round(batteryEnergyRequest, 2);

			System.out.println("\ngridEnergyRequest: "+gridEnergyRequest);
			System.out.println("\n derEnergyRequest: -"+derEnergyRequest);
			System.out.println("\n batteryEnergyRequest: "+batteryEnergyRequest);
			System.out.println("\n loadEnergyRequest: "+loadEnergyRequest);
			
			costKwh = derEnergyRequest > loadEnergyRequest ? GeneralData.getSellEnergyPrice()+derData.getCostKwh()
					: prices.get(0).getEnergyPrice();

			ResultPowerPrice derResult = new ResultPowerPrice(msgData.getDatetime(), derEnergyRequest, costKwh);
			ResultPowerPrice batteryResult = new ResultPowerPrice(msgData.getDatetime(), batteryEnergyRequest, costKwh);
			ResultPowerPrice loadResult = new ResultPowerPrice(msgData.getDatetime(), loadEnergyRequest, costKwh);
			
			
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "BatteryAggregatorAgent",
					"result", batteryResult);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
					"result", derResult);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
					"result", loadResult);
			
			
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
		derEnergyRequest = gridEnergyRequest > prices.get(0).getThreshold() ? derData.getUpperLimit() : derEnergyRequest;
		gridEnergyRequest = batteryEnergyRequest+loadEnergyRequest-derEnergyRequest;
		
		if(gridEnergyRequest > prices.get(0).getThreshold()) //if is still over the threshold
		{
			batteryEnergyRequest = -batteryData.getUpperLimit() > (gridEnergyRequest - prices.get(0).getThreshold()) 
					? -(gridEnergyRequest - prices.get(0).getThreshold()) : batteryData.getUpperLimit();
		}
		gridEnergyRequest = batteryEnergyRequest+loadEnergyRequest-derEnergyRequest;
		
		loadEnergyRequest = gridEnergyRequest > prices.get(0).getThreshold() ? loadData.getLowerLimit() : loadEnergyRequest;
		gridEnergyRequest = GeneralData.round(loadEnergyRequest+batteryEnergyRequest-derEnergyRequest, 2);
	}

	private double checkPercentile(double currentEnergyPrice)
	{
		int counter = 0;
		ArrayList<TimePowerPrice> lastWeekPrices = new DbTimePowerPrice().getLastWeekPowerPrice(prices.get(0).getDatetime());
		
		for(int i=0; i < lastWeekPrices.size(); i++)
		{
			if(currentEnergyPrice > lastWeekPrices.get(i).getEnergyPrice())
				counter++;
		}
		double percentile = (double)counter/lastWeekPrices.size();

		return percentile;
	}

}
