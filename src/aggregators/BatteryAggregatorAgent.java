package aggregators;

import agents.BaseAgent;
import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class BatteryAggregatorAgent extends BaseAgent {

	protected void setup(){
		
		registerDfAgent(this.getHap(), "BatteryAggregatorAgent", "AggregatorAgent");
		this.addBehaviour(new ReceiveMessages(this));
		
	}
	
}
