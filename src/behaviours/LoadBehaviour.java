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
		//System.out.println("\nloadBeh id: "+loadInfo.getIdLoad()+" prima: "+msgData.getDatetime().getTime());
		LoadData loadData = new DbLoadData().getLastLoadData(loadInfo.getIdLoad(), msgData.getDatetime());
		//System.out.println("\nloadBeh id: "+loadInfo.getIdLoad()+" dopo: "+msgData.getDatetime().getTime());
		
		if(msgData.getPowerRequested() < loadData.getConsumptionMin() || 
				msgData.getPowerRequested() > loadData.getConsumptionMax())
		{
			System.out.println("\nI'm "+this.myAgent.getName()+" and Control Agent requested "+msgData.getPowerRequested()+" Kw from me"
				+ "\nMy limits are: loadData.getConsumptionMin(): "+loadData.getConsumptionMin()+" loadData.getConsumptionMax():"+loadData.getConsumptionMax()
				+ "\nfor datetime: "+msgData.getDatetime().getTime());
			OkData ko = new OkData(msgData.getDatetime(), "load", 0);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
					"ok", ko);
			return;
		}
		
		double consumptionShifted = loadData.getConsumptionMax() - msgData.getPowerRequested();
		
		LoadData newLoadData = new LoadData(loadInfo.getIdLoad(), msgData.getDatetime(),
				msgData.getPowerRequested(), consumptionShifted);
		new DbLoadData().updateLoadDataPower(newLoadData);
		
		if(msgData.getPowerRequested() < loadData.getConsumptionMax())
		{
			LoadInfo newLoadInfo = new LoadInfo(loadInfo.getIdLoad(), loadData.getToDatetime(), consumptionShifted);
			new DbLoadInfo().updateLoadInfo(newLoadInfo);
		}
		
		OkData ok = new OkData(msgData.getDatetime(), "load", 1);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"ok", ok);
	}
}
