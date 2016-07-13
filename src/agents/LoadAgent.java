package agents;

import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class LoadAgent extends BaseAgent {

	protected void setup(){

		registerDfAgent(this.getHap(), "LoadAgent", "LoadAgent1"); //TO-DO
		this.addBehaviour(new ReceiveMessages(this));
	}
}
