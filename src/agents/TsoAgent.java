package agents;

import behaviours.TsoBehaviour;

@SuppressWarnings("serial")
public class TsoAgent extends BaseAgent {
	protected void setup(){

		registerDfAgent(this.getHap(), "TsoAgent");
		this.addBehaviour(new TsoBehaviour());
	}
}
