package behaviours;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.DerData;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import database.DbAggregatorLoad;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class AggregateDerBehaviour extends OneShotBehaviour {

	ACLMessage msg;
	FlexibilityData msgData;
	public AggregateDerBehaviour(ACLMessage msg) 
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
		DerData derData = new DbDerInfo().getDerDataByIdAgent(msg.getSender().getName());
		AggregatorFlexibilityData data = new AggregatorFlexibilityData(this.myAgent.getName(), 
				derInfo.getIdLoad(), msgData);
		new DbAggregatorLoad().addFlexibilityLoadMessage(data);
		
		int messagesReceived = new DbAggregatorLoad().countMessagesReceived(this.myAgent.getName());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		
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
