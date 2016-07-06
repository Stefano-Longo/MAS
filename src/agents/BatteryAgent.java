package agents;

import basicData.BatteryInfo;
import behaviours.ReceiveMessages;
import database.DbBatteryInfo;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BatteryAgent extends BaseAgent {
	
	protected void setup(){
		/**
		 * Takes in input the ids (number) of the platforms for which it is created
		 */
		
		registerDfAgent(this.getHap(), "BatteryAgent");
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(this.getName());
		registerDfAgent(this.getHap(), "BatteryAgent-"+batteryInfo.getIdBattery());
		//serve per il disaggregatorBattery
		this.addBehaviour(new ReceiveMessages(this));
		
	}
}
