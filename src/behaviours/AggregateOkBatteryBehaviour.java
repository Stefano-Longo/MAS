package behaviours;

import agents.BaseAgent;
import basicData.BatteryInfo;
import basicData.OkData;
import database.DbAggregatorBattery;
import database.DbBatteryInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AggregateOkBatteryBehaviour extends OneShotBehaviour {
	
	ACLMessage msg;
	OkData msgData;
	public AggregateOkBatteryBehaviour(ACLMessage msg) 
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

		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryInfoByIdAgent(msg.getSender().getName());
		
		new DbAggregatorBattery().updateLastBatteryConfirmedChoice(this.myAgent.getName(), 
				batteryInfo.getIdBattery(), msgData.getOk());
		
		int confirmedTrue = new DbAggregatorBattery().getLastConfirmedByChoice(this.myAgent.getName(),
				batteryInfo.getIdBattery(), true);
		int batteryAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "BatteryAgent").length;
		System.out.println("BatteryOk - confirmedTrue:"+confirmedTrue+" batteryAgents: "+batteryAgents);
		if(confirmedTrue == batteryAgents)
		{
			OkData ok = new OkData(msgData.getDatetime(), "battery", true);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"ok", ok);
		}
	}
}
