package aggregators;

import agents.BaseAgent;
import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class DerAggregatorAgent extends BaseAgent {

	protected void setup(){

		registerDfAgent(this.getHap(), "DerAggregatorAgent", "AggregatorAgent");
		this.addBehaviour(new ReceiveMessages(this));
	}
}
