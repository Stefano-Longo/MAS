package agents;

import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class DerAgent extends BaseAgent {

	protected void setup(){
		
		registerDfAgent(this.getHap(), "DerAgent");
		addBehaviour(new ReceiveMessages(this));

	}
}
