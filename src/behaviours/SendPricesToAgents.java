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

	/**
	 * Vedere come usare platform!!
	 */
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

		/**
		 * Receive the message from the GridAgent, save the content in a variable
		 * and then it sends the same (?) message to the aggregators
		 * 
		 * if there are no agents which offer that service, then I respond to the ControlAgent saying
		 * "I have no flexibility"
		 */

		DFAgentDescription[] agents = new BaseAgent().getAgentsbyServiceType(this.myAgent, serviceType);
		
		if(agents.length == 0)
		{
			/*AggregatorFlexibilityData result = new AggregatorFlexibilityData(this.myAgent.getName(), 0, 
					msgData.get(0).getDateTime(), 0, 0, 0, 0);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);*/
		}
		else
		{
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, serviceType,
					"input", msgData);
		}
	}
}
