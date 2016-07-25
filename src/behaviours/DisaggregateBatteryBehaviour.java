package behaviours;

import java.io.IOException;
import java.util.ArrayList;
import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.BatteryInfo;
import basicData.ResultPowerPrice;
import database.DbAggregatorBattery;
import database.DbBatteryInfo;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class DisaggregateBatteryBehaviour extends OneShotBehaviour {

	ACLMessage msg;
	ResultPowerPrice msgData;
	ArrayList<AggregatorFlexibilityData> batteriesChoice = new DbAggregatorBattery()
			.getBatteriesChoice(this.myAgent.getName());
	
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
		
		/**
		 * ONLY FLEXIBILITY:
		 * Aggregator takes upperLimit energy from the most convenient Battery, and do the same with the 
		 * second etc until he finishes to take all the energy that the control agent needs.
		 * 
		 * Aggregator takes the energy from the most convenient Batteries.
		 */
		
		
		DFAgentDescription[] batteryAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "BatteryAgent");
		
		if(batteriesChoice.size() == batteryAgents.length)
		{
			if(msgData.getPowerRequested() > 0)
			{
				takeFromMostConvenient();
			}
			else if(msgData.getPowerRequested() < 0)
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
		//TO-DO prova se funziona ORDERLIST
		batteriesChoice.sort((o1, o2) -> Double.compare(o1.getCostKwh(),o2.getCostKwh()));
		// the first battery is the one with lower CostKwh

		for(int i=0; i < batteriesChoice.size(); i++)
		{
			//ControlAgent sends this: datetime, power, CostKwh
			if(totalPowerRequested > batteriesChoice.get(i).getUpperLimit())
			{
				batteryPowerRequested = batteriesChoice.get(i).getUpperLimit();
				totalPowerRequested -= batteriesChoice.get(i).getUpperLimit();
			}
			else 
			{
				batteryPowerRequested = totalPowerRequested;
				totalPowerRequested = 0;
			} 
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), batteryPowerRequested, msgData.getCostKwh());
			
			BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdBattery(batteriesChoice.get(i).getIdentificator());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					batteryInfo.getIdAgent(), "response", batteryAction);
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

		ArrayList<AggregatorFlexibilityData> batteriesNegativeChoice = new DbAggregatorBattery()
				.getBatteriesChoiceByValue(this.myAgent.getName(), "negative");
		double aggregateChoice = -batteriesNegativeChoice.get(0).getDesideredChoice();
		double powerRequested = -msgData.getPowerRequested();
		double batteryPowerGiven = 0;
		
		double batteriesDesiderDischarge = batteriesChoice.size() - batteriesNegativeChoice.size();

		for(int i=0; i < batteriesChoice.size(); i++)
		{
			if(!batteriesNegativeChoice.contains(batteriesChoice.get(i)))
			{ 	
				/**
				 * le batterie che si vogliono scaricare sono alla fine della lista (diff maggiore), 
				 * quindi divido la parte rimanente tra loro
				 */
				if(powerRequested > 0)
				{
					if(powerRequested/batteriesDesiderDischarge < -batteriesChoice.get(i).getLowerLimit())
					{
						batteryPowerGiven = powerRequested/batteriesDesiderDischarge;
					}
					else 
					{
						batteryPowerGiven = -batteriesChoice.get(i).getLowerLimit();
					}
					if(batteryPowerGiven > powerRequested)
					{
						batteryPowerGiven = powerRequested;
					}
					powerRequested -= batteryPowerGiven;
				}
			}
			else if(powerRequested > 0)
			{
				double percentage = (powerRequested-aggregateChoice)/aggregateChoice;
				batteryPowerGiven = -batteriesChoice.get(i).getDesideredChoice(); 
				if((batteryPowerGiven + batteryPowerGiven*percentage) < -batteriesChoice.get(i).getLowerLimit())
				{
					batteryPowerGiven += batteryPowerGiven*percentage; //aggiungo o tolgo la percentuale
				}
				else
				{
					batteryPowerGiven = -batteriesChoice.get(i).getLowerLimit();
				}
				if(batteryPowerGiven > powerRequested)
				{
					batteryPowerGiven = powerRequested;
				}
				aggregateChoice -= batteryPowerGiven; //TO-DO vedi che può essere negativa!! perché tolgo sempre
				powerRequested -= batteryPowerGiven;
			}
			
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), batteryPowerGiven, msgData.getCostKwh());

			BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdBattery(batteriesChoice.get(i).getIdentificator());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					batteryInfo.getIdAgent(), "response", batteryAction);
		}
	}
	
	/**
	 * Send all the batteries 0 as value
	 */
	private void doNothing()
	{
		for(int i=0; i < batteriesChoice.size(); i++)
		{
			ResultPowerPrice batteryAction = new ResultPowerPrice(msgData.getDatetime(), 0, msgData.getCostKwh());

			BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdBattery(batteriesChoice.get(i).getIdentificator());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					batteryInfo.getIdAgent(), "response", batteryAction);
		}
	}
}
