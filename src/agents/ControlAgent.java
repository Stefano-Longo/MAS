package agents;

import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class ControlAgent extends BaseAgent {

	protected void setup(){

		registerDfAgent(this.getHap(), "ControlAgent");
		this.addBehaviour(new ReceiveMessages(this));
		
	}
}
