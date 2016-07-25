package behaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class DisaggregateDerBehaviour extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ACLMessage msg;
	public DisaggregateDerBehaviour(ACLMessage msg) {
		this.msg = msg;
	}
	
	@Override
	public void action() {
		/**
		 * I ask first the maximum from solar, wind and hydro. Then I ask to generator if needed
		 */
		
		
	}

}
