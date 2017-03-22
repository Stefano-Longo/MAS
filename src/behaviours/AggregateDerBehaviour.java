package behaviours;

import agents.BaseAgent;
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
		new DbAggregatorDer().addFlexibilityDerMessage(this.myAgent.getName(), msgData);
		
		int messagesReceived = new DbAggregatorDer().countMessagesReceived(this.myAgent.getName(), msgData.getDatetime());
		int derAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "DerAgent").length;
		
		//System.out.println("DER messagesReceived: "+messagesReceived+" derAgents: "+derAgents);
		if (messagesReceived == derAgents)
		{
			//System.out.println("messages: "+messagesReceived+" derAgents: "+derAgents);
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */
			
			FlexibilityData result = new DbAggregatorDer().
					aggregateMessagesReceived(this.myAgent.getName(), msgData.getDatetime());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}
}
