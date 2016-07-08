package agents;

import behaviours.CalculateLoadFlexibilityBehaviour;
import behaviours.ReceiveMessages;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class LoadAgent extends BaseAgent {

	protected void setup(){

		registerDfAgent(this.getHap(), "LoadAgent", "LoadAgent1"); //TO-DO
		this.addBehaviour(new ReceiveMessages(this));
	}
}
