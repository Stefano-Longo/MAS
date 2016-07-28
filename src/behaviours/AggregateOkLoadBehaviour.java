package behaviours;

import agents.BaseAgent;
import basicData.BatteryInfo;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.OkData;
import database.DbAggregatorBattery;
import database.DbAggregatorLoad;
import database.DbBatteryInfo;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

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
		
		new DbAggregatorLoad().updateLastLoadConfirmedChoice(this.myAgent.getName(), 
				loadInfo.getIdLoad(), msgData.getOk());
		
		int confirmedTrue = new DbAggregatorLoad().getLastConfirmedByChoice(this.myAgent.getName(), true);
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		System.out.println("LoadOk - confirmedTrue:"+confirmedTrue+" loadAgents: "+loadAgents);
		if(confirmedTrue == loadAgents)
		{
			OkData ok = new OkData(msgData.getDatetime(), "load", true);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"ok", ok);
		}
	}
}
