package agents;

import basicData.BatteryInfo;
import behaviours.ReceiveMessages;
import database.DbBatteryInfo;

@SuppressWarnings("serial")
public class BatteryAgent extends BaseAgent {
	
	protected void setup(){
		/**
		 * Takes in input the ids (number) of the platforms for which it is created
		 */
		
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(this.getName());
		registerDfAgent(this.getHap(), "BatteryAgent", "BatteryAgent-"+batteryInfo.getIdBattery());

		this.addBehaviour(new ReceiveMessages(this));
		
	}
}
