package behaviours;

import java.security.KeyStore.LoadStoreParameter;

import agents.BaseAgent;
import basicData.LoadInfo;
import basicData.OkData;
import database.DbAggregatorLoad;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AggregateOkLoadBehaviour extends OneShotBehaviour {
	
	ACLMessage msg;
	OkData msgData;
	public AggregateOkLoadBehaviour(ACLMessage msg) 
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
		
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(msg.getSender().getName(), msgData.getDatetime());
		//System.out.println("\nLoadAggrOK prima: "+msgData.getDatetime().getTime());
		new DbAggregatorLoad().updateLastLoadConfirmedChoice(this.myAgent.getName(), 
				loadInfo.getIdLoad(), msgData.getOk(), msgData.getDatetime());
		//System.out.println("\nLoadAggrOK dopo: "+msgData.getDatetime().getTime());
		
		int confirmedTrue = new DbAggregatorLoad().getLastConfirmedByChoice(this.myAgent.getName(), 1, msgData.getDatetime());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		System.out.println("LoadOk - confirmedTrue:"+confirmedTrue+" loadAgents: "+loadAgents);
		if(confirmedTrue == loadAgents)
		{
			OkData ok = new OkData(msgData.getDatetime(), "load", 1);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"ok", ok);
		}
	}
}
