package behaviours;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.BatteryInfo;
import basicData.ResultPowerPrice;
import database.DbAggregatorBattery;
import database.DbBatteryInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class DisaggregateBatteryBehaviour extends OneShotBehaviour {

	ACLMessage msg;
	ResultPowerPrice msgData;
	ArrayList<AggregatorFlexibilityData> batteryChoices = new ArrayList<AggregatorFlexibilityData>();

	public DisaggregateBatteryBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() 
	{
		/**
		 * Get what the batteryAgents wanted to do for this hour and send them a message saying what to do 
		 * It should be as near as possible to what they wanted to do
		 * 
		 * Linear Programming -> Operative Research
		 */
		
		//BatteryOutput -> powerRequested negative value
		//BatteryInput -> powerRequested positive value
		
		batteryChoices = new DbAggregatorBattery().getBatteriesChoice(this.myAgent.getName());
		DFAgentDescription[] batteryAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "BatteryAgent");
		
		if(batteryChoices.size() == batteryAgents.length)
		{
			if(msgData.getPowerRequested() < 0) //output from battery
			{
				takeFromMostConvenient();
			}
			else if(msgData.getPowerRequested() > 0) //input in battery
			{
				giveWhatAsked();
			}
			else if(msgData.getPowerRequested() == 0)
			{
				doNothing();
			}
		}
		
	}
	
	private void takeFromMostConvenient()
	{
		double totalPowerRequested = msgData.getPowerRequested();
		double batteryPowerRequested = 0;
		batteryChoices.sort((o1, o2) -> Double.compare(o1.getCostKwh(),o2.getCostKwh()));
		// the first battery is the one with lower CostKwh

		for(int i=0; i < batteryChoices.size(); i++)
		{
			batteryPowerRequested = totalPowerRequested < batteryChoices.get(i).getUpperLimit() ?
					batteryChoices.get(i).getUpperLimit() : totalPowerRequested;

			totalPowerRequested -= batteryPowerRequested; 
			 
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), batteryPowerRequested, msgData.getCostKwh());
			sendMessage(batteryAction, i);
		}
	}

	/**
	 * I try to give to all batteries what they asked
	 * 
	 * batteriesChoice list is ordered by difference UpperLimit-DesideredChoice
	 * why all of this? because some batteries can have the upperLimit equal to the desideredChoice, others maybe not.
	 * So I take first the maximum (so the desidered choice) from the ones which requested the maximum,
	 * then I apply the percentage to all the others.
	 * 
	 * If some batteries desire to discharge then I'll charge them with the same quantity of kw
	 * for the next slotTime
	 */
	private void giveWhatAsked()
	{
		// percentuale da aggiungere (o togliere, in base al segno) ad ogni scelta delle batterie
		//ATTENZIONE: passo i valori da negativi a positivi, alla fine li passo negativi di nuovo

		ArrayList<AggregatorFlexibilityData> batteriesPositiveChoice = new DbAggregatorBattery()
				.getBatteriesChoiceByValue(this.myAgent.getName(), "positive", msgData.getDatetime());
		double powerRequested = msgData.getPowerRequested();
		double batteryPowerGiven = 0;
		
		//numero di batterie che vogliono caricarsi
		double batteriesDesiderCharge = batteryChoices.size() - batteriesPositiveChoice.size();

		for(int i=0; i < batteryChoices.size(); i++)
		{
			batteryPowerGiven = 0;
			
			if(batteriesPositiveChoice.contains(batteryChoices.get(i)))
			{ 	
				/**
				 * le batterie che si vogliono scaricare sono alla fine della lista (diff maggiore), 
				 * quindi divido la parte rimanente tra loro
				 */
				if(powerRequested > 0)
				{
					if(powerRequested/batteriesDesiderCharge < batteryChoices.get(i).getLowerLimit())
					{
						batteryPowerGiven = powerRequested/batteriesDesiderCharge;
					}
					else 
					{
						batteryPowerGiven = batteryChoices.get(i).getLowerLimit();
					}
					if(batteryPowerGiven > powerRequested)
					{
						batteryPowerGiven = powerRequested;
					}
				}
			}
			else if(powerRequested > 0)
			{
				batteryPowerGiven = powerRequested > batteryChoices.get(i).getLowerLimit() ? 
						batteryChoices.get(i).getLowerLimit() : powerRequested;
			}
			powerRequested -= batteryPowerGiven;
			
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), batteryPowerGiven, msgData.getCostKwh());
			sendMessage(batteryAction, i);
		}
	}
	
	/**
	 * Send all the batteries 0 as value
	 */
	private void doNothing()
	{
		for(int i=0; i < batteryChoices.size(); i++)
		{
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), 0, msgData.getCostKwh());

			BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryInfoByIdBattery(batteryChoices.get(i).getIdentificator());
			String shortName = new BaseAgent().getShortName(batteryInfo.getIdAgent());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					this.myAgent.getAID(shortName), "result", batteryAction);
		}
	}
	
	private void sendMessage(ResultPowerPrice batteryAction, int counter)
	{
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryInfoByIdBattery(batteryChoices.get(counter).getIdentificator());
		String shortName = new BaseAgent().getShortName(batteryInfo.getIdAgent());
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
				this.myAgent.getAID(shortName), "result", batteryAction);
	}
}
