package behaviours;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import database.DbAggregatorLoad;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AggregateLoadBehaviour extends OneShotBehaviour {
	
	ACLMessage msg;
	FlexibilityData msgData;
	public AggregateLoadBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (FlexibilityData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() 
	{
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(msg.getSender().getName(), msgData.getDatetime());
		AggregatorFlexibilityData data = new AggregatorFlexibilityData(this.myAgent.getName(), 
				loadInfo.getIdLoad(), msgData);
		new DbAggregatorLoad().addFlexibilityLoadMessage(data);
		
		int messagesReceived = new DbAggregatorLoad().countMessagesReceived(this.myAgent.getName());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		
		System.out.println("DER messagesReceived: "+messagesReceived+" derAgents: "+loadAgents);

		if (messagesReceived == loadAgents)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */
			
			FlexibilityData result = new DbAggregatorLoad().
					aggregateMessageReceived(this.myAgent.getName());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}
}
