package behaviours;

import agents.BaseAgent;
import basicData.FlexibilityData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import database.DbAggregatorBattery;

@SuppressWarnings("serial")
public class AggregateBatteryBehaviour extends OneShotBehaviour {

	/**
	 * save the data arrived by messages in the db and checks if the number of mex received is the 
	 * same of the number of battery agents. If it's the same, then send the total to the control behaviour.
	 * 
	 * 
	 * it saves the real data for the next hour (period of time) and sends to the control agent everything 
	 * taking this "everything" from the db
	 */

	ACLMessage msg;
	FlexibilityData msgData;
	public AggregateBatteryBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (FlexibilityData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	public void action() 
	{
		//System.out.println("AggregateBatteryBeh START - msgData.getDatetime() = "+msgData.getDatetime().getTime());

		new DbAggregatorBattery().addFlexibilityBatteryMessage(this.myAgent.getName(), msgData);
		
		int messagesReceived = new DbAggregatorBattery().countMessagesReceived(this.myAgent.getName(), msgData.getDatetime());
		int batteryAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "BatteryAgent").length;
		
		//System.out.println("AggregateBatteryBeh END - msgData.getDatetime() = "+msgData.getDatetime().getTime());

		//System.out.println("BATTERY messagesReceived: "+messagesReceived+", batteryAgents: "+batteryAgents);
		if (messagesReceived == batteryAgents)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */
			
			FlexibilityData result = new DbAggregatorBattery().
					aggregateMessagesReceived(this.myAgent.getName(), msgData.getDatetime());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}

	
		/**
		 * waits until the BatteryAgents answer and then sum their result. 
		 * If some of them didn't answer after (n)seconds then send again the initial message.
		 */

}
