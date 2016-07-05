package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
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
		
		ArrayList<FlexibilityData> list = new DbAggregatorBattery().getBatteryChoice(this.myAgent.getName());
		DFAgentDescription[] ca = new BaseAgent().getAgentsbyServiceType(myAgent, "BatteryAgent");
		
		if(list.size() == ca.length)
			System.out.println("TUTTO OK");
		else //quella che ho in meno o in più la ignoro. Però segnalo il dato mancante o eccedente, come?
			System.out.println("Il numero di batteryAgent registrati è inferiore al numero di dati nel db");
	
		
	}

}
