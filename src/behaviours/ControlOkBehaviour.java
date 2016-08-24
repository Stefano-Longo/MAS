package behaviours;

import agents.BaseAgent;
import basicData.ControlData;
import basicData.OkData;
import basicData.ResultPowerPrice;
import database.DbControlArrivalData;
import database.DbControlData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class ControlOkBehaviour extends OneShotBehaviour {

	ACLMessage msg;
	OkData msgData;
	public ControlOkBehaviour(ACLMessage msg) {
		this.msg = msg;
		try {
			msgData = (OkData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		new DbControlArrivalData().updateControlArrivalData(this.myAgent.getName(), msgData.getType(), msgData.getOk());
		int confirmedTrue = new DbControlArrivalData().getLastConfirmedByChoice(this.myAgent.getName(), true);
		System.out.println("\nControlOkBehaviour: confirmedTrue: "+confirmedTrue);

		if (confirmedTrue == 3)
		{
			System.out.println("entrato");
			ControlData controlData = new DbControlData().getLastControlDatabyIdAgent(this.myAgent.getName());
			ResultPowerPrice gridResult = new ResultPowerPrice(controlData.getDatetime(), 
					controlData.getGridPower(), controlData.getCostKwh());
			new DbControlData().setConfirmed(this.myAgent.getName());
			
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "GridAgent",
					"result", gridResult);
		}
	}
}
