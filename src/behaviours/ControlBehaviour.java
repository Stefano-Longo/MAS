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
import database.DbGridData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class ControlBehaviour extends OneShotBehaviour {

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
			ArrayList<TimePowerPrice> prices = new DbGridData().getPriceData(controlArrivalData.getDatetime());
			ControlFlexibilityData batteryData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "battery");
			ControlFlexibilityData derData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "der");
			ControlFlexibilityData loadData = new DbControlArrivalData().getLastControlArrivalData(this.myAgent.getName(), "load");
			
			double meanPrice = calculateMeanPrice(prices);
			System.out.println("meanPrice :"+meanPrice);
			double derEnergyRequest = 0;
			double loadEnergyRequest = 0;
			double batteryEnergyRequest = 0;
			double costKwh = 0;
			double gridEnergyRequest = 0;
			if(meanPrice > prices.get(0).getEnergyPrice() + 0.2*meanPrice)
			{
				System.out.println("Now buy energy from the grid - no use battery, maybe charge it");
				/**
				 * Now buy energy from the grid - no use battery, maybe charge it
				 */
				derEnergyRequest = derData.getDesideredChoice();
				loadEnergyRequest = loadData.getUpperLimit();
				
				//se la produzione � maggiore del carico, carico le batterie
				if (-derEnergyRequest > loadEnergyRequest)
				{
					System.out.println("La produzione in eccedenza la metto in batteria finch� possibile");
					batteryEnergyRequest = (-derEnergyRequest - loadEnergyRequest) > batteryData.getUpperLimit()
							? batteryData.getUpperLimit() : (-derEnergyRequest - loadEnergyRequest);
				}
				
				//se sono molto scariche e se conviene assai, carico le batterie, altrimenti no
				if(batteryData.getDesideredChoice() > 0 &&
						meanPrice > prices.get(0).getEnergyPrice() + 0.5*meanPrice)
				{
					System.out.println("I charge batteries as much as they want to");
					batteryEnergyRequest = batteryData.getDesideredChoice() > 0 ? 
							batteryData.getDesideredChoice() : batteryEnergyRequest;
				}
				costKwh = prices.get(0).getEnergyPrice();
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
			}
			
			gridEnergyRequest = new GeneralData().round(loadEnergyRequest+batteryEnergyRequest-derEnergyRequest, 2);

			System.out.println("\n\nType    -   Datetime       -   LowerLimit  -  UpperLimit  "
					+ "DesideredChoice    -   CostKwh");
			System.out.println(batteryData.getType()+" "+batteryData.getDatetime().getTime()+" "+batteryData.getLowerLimit()+" "+batteryData.getUpperLimit()+
					" "+batteryData.getDesideredChoice()+" "+batteryData.getCostKwh());
			System.out.println(derData.getType()+" "+derData.getDatetime().getTime()+" "+derData.getLowerLimit()+" "+derData.getUpperLimit()+
					" "+derData.getDesideredChoice()+" "+derData.getCostKwh());
			System.out.println(loadData.getType()+" "+loadData.getDatetime().getTime()+" "+loadData.getLowerLimit()+" "+loadData.getUpperLimit()+
					" "+loadData.getDesideredChoice()+" "+loadData.getCostKwh());
			
			System.out.println("\ngridEnergyRequest: "+gridEnergyRequest);
			System.out.println("\n derEnergyRequest: "+-derEnergyRequest);
			System.out.println("\n batteryEnergyRequest: "+batteryEnergyRequest);
			System.out.println("\n loadEnergyRequest: "+loadEnergyRequest);
			
			ResultPowerPrice derResult = new ResultPowerPrice(msgData.getDatetime(), -derEnergyRequest, costKwh);
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
	private boolean peakShaving()
	{
		boolean ok = false;
		return ok;
	}
	
	private double calculateMeanPrice (ArrayList<TimePowerPrice> prices)
	{
		float sum=0;
		for(int i=0; i<prices.size(); i++)
		{
			sum += prices.get(i).getEnergyPrice();
		}
		return new GeneralData().round(sum/prices.size(), 4);
	}

}
