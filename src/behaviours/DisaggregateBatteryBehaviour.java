package behaviours;

import java.io.IOException;
import java.util.ArrayList;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.FlexibilityData;
import basicData.ResultData;
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
	ResultData msgData;
	public DisaggregateBatteryBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (ResultData)msg.getContentObject();
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
		
		ArrayList<AggregatorFlexibilityData> list = new DbAggregatorBattery().getBatteriesChoice(this.myAgent.getName());
		DFAgentDescription[] batteryAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "BatteryAgent");
		
		if(list.size() != batteryAgents.length)
			System.out.println("Il numero di batteryAgent registrati è inferiore al numero di dati nel db");
		else{ //quella che ho in meno o in più la ignoro. Però segnalo il dato mancante o eccedente, come?
			System.out.println("TUTTO OK");
	
			try {
			//per ora invio come scelta quella fatta da loro
				//ATTENZIONE far matchare l'id battery con quello a cui invio!!
				for(int i=0; i<list.size(); i++)
				{
					//prende la batteria giusta, quella che ha il servizio BatteryAgent-1
					DFAgentDescription[] battery = new BaseAgent().getAgentsbyServiceType(myAgent,
							"BatteryAgent"+list.get(i).getIdentificator());
					
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.setContentObject(list.get(i));
					message.addReceiver(battery[0].getName());
					this.myAgent.send(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
