package behaviours;

import agents.BaseAgent;
import basicData.DerData;
import basicData.DerInfo;
import basicData.OkData;
import basicData.ResultPowerPrice;
import database.DbDerData;
import database.DbDerInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class DerBehaviour extends OneShotBehaviour {
	
	private static final long serialVersionUID = 1L;

	ResultPowerPrice msgData;
	public DerBehaviour(ACLMessage msg) {
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		
		DerInfo derInfo = new DbDerInfo().getDerInfoByIdAgent(this.myAgent.getName());
		DerData derData = new DbDerData().getLastDerData(derInfo.getIdDer());
		
		DerData newDerData = new DerData(derInfo.getIdDer(), msgData.getDatetime(),
					msgData.getPowerRequested());	

		//attention! The value is negative
		System.out.println("\nI'm "+this.myAgent.getName()+" and Control Agent requested "+msgData.getPowerRequested()+" Kw from me");
		System.out.println("My limits are: derData.getProductionMin(): "+derData.getProductionMin()+" derData.getProductionMax():"+derData.getProductionMax());
		
		if(msgData.getPowerRequested() < derData.getProductionMin() || 
				msgData.getPowerRequested() > derData.getProductionMax())
		{
			OkData ko = new OkData(msgData.getDatetime(), "der", false);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
					"ok", ko);
			return;
		}
		
		new DbDerData().updateDerData(newDerData);
		
		OkData ok = new OkData(msgData.getDatetime(), "der", true);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
				"ok", ok);
	}
}
