package behaviours;

import java.io.IOException;
import java.util.ArrayList;
import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.ResultPowerPrice;
import database.DbAggregatorBattery;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class DisaggregateBatteryBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ACLMessage msg;
	ResultPowerPrice msgData;
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
		 * Aggregator gives the energy at the most convenient Batteries.
		 */
		
		ArrayList<AggregatorFlexibilityData> batteriesChoice = new DbAggregatorBattery().getBatteriesChoice(this.myAgent.getName());
		DFAgentDescription[] batteryAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "BatteryAgent");
		
		if(batteriesChoice.size() != batteryAgents.length)
		{
			System.out.println("Il numero di batteryAgent registrati è inferiore al numero di dati nel db");
		}
		else
		{ //quella che ho in meno o in più la ignoro. Però segnalo il dato mancante o eccedente, come?
			System.out.println("TUTTO OK");
	
			try {
				//ATTENZIONE far matchare l'id battery con quello a cui invio!!
				double totalPowerRequested = msgData.getPowerRequested();
				double batteryPowerRequested = 0;
				for(int i=0; i < batteriesChoice.size(); i++)
				{
					// the first battery is the one with lower CostKwh
					
					//prende la batteria giusta, quella che ha il servizio BatteryAgent-1
					DFAgentDescription[] battery = new BaseAgent().getAgentsbyServiceType(myAgent,
							"BatteryAgent-"+batteriesChoice.get(i).getIdentificator());
					
					//ControlAgent sends this: Power, time, CostKwh
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
					ResultPowerPrice batteryAction = new ResultPowerPrice(batteryPowerRequested, msgData.getCostKwh());
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setContentObject(batteryAction);
					message.addReceiver(battery[0].getName());
					this.myAgent.send(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
