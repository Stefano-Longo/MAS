package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class SendPricesToAgents extends OneShotBehaviour {

	ACLMessage msg;
	ArrayList<TimePowerPrice> msgData;
	String serviceType;
	public SendPricesToAgents(ACLMessage msg, String serviceType){
		this.msg = msg;
		this.serviceType = serviceType;
		try {
			this.msgData = (ArrayList<TimePowerPrice>) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, serviceType,
			"input", msgData);
	
	}
}
