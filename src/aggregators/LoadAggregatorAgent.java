package aggregators;

import agents.BaseAgent;
import behaviours.ReceiveMessages;

public class LoadAggregatorAgent extends BaseAgent {

	private static final long serialVersionUID = 1L;

	protected void setup(){
		
		registerDfAgent(this.getHap(), "LoadAggregatorAgent", "AggregatorAgent");
		this.addBehaviour(new ReceiveMessages(this));
	}
}
