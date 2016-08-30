package behaviours;

import agents.BaseAgent;
import basicData.LoadData;
import basicData.LoadInfo;
import basicData.OkData;
import basicData.ResultPowerPrice;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class LoadBehaviour extends OneShotBehaviour {

	ResultPowerPrice msgData;
	public LoadBehaviour(ACLMessage msg) {
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), msgData.getDatetime());
		LoadData loadData = new DbLoadData().getLastLoadData(loadInfo.getIdLoad());
			
		if(msgData.getPowerRequested() < loadData.getConsumptionMin() || 
				msgData.getPowerRequested() > loadData.getConsumptionMax())
		{
			OkData ko = new OkData(msgData.getDatetime(), "load", false);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
					"ok", ko);
			return;
		}
		
		double consumptionShifted = loadData.getConsumptionMax() - msgData.getPowerRequested();
		
		LoadData newLoadData = new LoadData(loadInfo.getIdLoad(), msgData.getDatetime(),
				msgData.getPowerRequested(), consumptionShifted);
		new DbLoadData().updateLoadData(newLoadData);
		
		if(msgData.getPowerRequested() < loadData.getConsumptionMax())
		{
			LoadInfo newLoadInfo = new LoadInfo(loadInfo.getIdLoad(), loadData.getToDatetime(), consumptionShifted);
			new DbLoadInfo().updateLoadInfo(newLoadInfo);
		}
		
		OkData ok = new OkData(msgData.getDatetime(), "load", true);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"ok", ok);
	}
}
