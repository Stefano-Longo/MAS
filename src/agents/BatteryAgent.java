package agents;

import behaviours.ReceiveMessages;

@SuppressWarnings("serial")
public class BatteryAgent extends BaseAgent {
	
	protected void setup(){
		/**
		 * Takes in input the ids (number) of the platforms for which it is created
		 */
		
		registerDfAgent(this.getHap(), "BatteryAgent");

		this.addBehaviour(new ReceiveMessages(this));
		
	}
}
