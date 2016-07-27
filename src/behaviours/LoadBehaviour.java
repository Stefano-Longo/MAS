package behaviours;

import basicData.LoadData;
import basicData.LoadInfo;
import basicData.ResultPowerPrice;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class LoadBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;

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
				
		double consumptionShifted = loadData.getConsumptionMax() - msgData.getPowerRequested();
		
		LoadData newLoadData = new LoadData(loadInfo.getIdLoad(), msgData.getDatetime(),
				msgData.getPowerRequested(), consumptionShifted);

		new DbLoadData().updateLoadData(newLoadData);
		
		LoadInfo newLoadInfo = new LoadInfo(loadInfo.getIdLoad(), loadData.getToDatetime(), consumptionShifted);
		new DbLoadInfo().updateLoadInfo(newLoadInfo);
	}
}
