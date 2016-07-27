package behaviours;

import agents.BaseAgent;
import basicData.DerInfo;
import basicData.OkData;
import database.DbAggregatorDer;
import database.DbDerInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AggregateOkDerBehaviour extends OneShotBehaviour {
	
	ACLMessage msg;
	OkData msgData;
	public AggregateOkDerBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (OkData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
			
		DerInfo derInfo = new DbDerInfo().getDerInfoByIdAgent(msg.getSender().getName());
		new DbAggregatorDer().updateLastDerConfirmedChoice(this.myAgent.getName(), 
				derInfo.getIdDer(), msgData.getOk());
		
		int confirmedTrue = new DbAggregatorDer().getLastConfirmedByChoice(this.myAgent.getName(),
				derInfo.getIdDer(), true);
		int derAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "DerAgent").length;
		
		if(confirmedTrue == derAgents)
		{
			OkData ok = new OkData(msgData.getDatetime(), "der", true);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"ok", ok);
		}
	}
	
}
