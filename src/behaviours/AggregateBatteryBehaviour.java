package behaviours;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.BatteryInfo;
import basicData.FlexibilityData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import database.DbAggregatorBattery;
import database.DbBatteryInfo;

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
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(msg.getSender().getName());
		AggregatorFlexibilityData data = new AggregatorFlexibilityData(this.myAgent.getName(), 
					batteryInfo.getIdBattery(), msgData);
		new DbAggregatorBattery().addFlexibilityBatteryMessage(data);
		
		int messagesReceived = new DbAggregatorBattery().countMessagesReceived(this.myAgent.getName());
		int batteryAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "BatteryAgent").length;
		if (messagesReceived == batteryAgents)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */
			
			FlexibilityData result = new DbAggregatorBattery().
					aggregateMessageReceived(this.myAgent.getName());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}

	
		/**
		 * waits until the BatteryAgents answer and then sum their result. 
		 * If some of them didn't answer after (n)seconds then send again the initial message.
		 */

}
