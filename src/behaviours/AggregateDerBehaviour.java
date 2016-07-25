package behaviours;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.DerInfo;
import basicData.FlexibilityData;
import database.DbAggregatorDer;
import database.DbDerInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
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
		DerInfo derInfo = new DbDerInfo().getDerInfoByIdAgent(msg.getSender().getName());
		AggregatorFlexibilityData data = new AggregatorFlexibilityData(this.myAgent.getName(), 
				derInfo.getIdDer(), msgData);
		new DbAggregatorDer().addFlexibilityDerMessage(data);
		
		int messagesReceived = new DbAggregatorDer().countMessagesReceived(this.myAgent.getName());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "DerAgent").length;
		
		if (messagesReceived == loadAgents)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */
			
			FlexibilityData result = new DbAggregatorDer().
					aggregateMessageReceived(this.myAgent.getName());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}
}
