package behaviours;

import basicData.LoadData;
import basicData.LoadInfo;
import basicData.ResultPowerPrice;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

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
		
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName());
		LoadData loadData = new DbLoadData().getLastLoadData(loadInfo.getIdLoad());
				
		double consumptionShifted = loadData.getConsumptionMax() - msgData.getPowerRequested();
		
		LoadData newloadData = new LoadData(loadInfo.getIdLoad(), msgData.getDatetime(),
				msgData.getPowerRequested(), consumptionShifted);

		new DbLoadData().updateLoadData(newloadData);
				
		/**
		 * TO-DO Aggiornare load info inserendo nel campo ConsumptionAdded della tabella Load
		 * nel DateTime = toDateTime il valore: consumptionShifted
		 */
		new DbLoadInfo().updateLoadInfo();
	}
}
