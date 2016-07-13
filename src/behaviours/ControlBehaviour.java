package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import aggregators.*;
import basicData.ControlFlexibilityData;
import basicData.FlexibilityData;
import database.DbAggregatorBattery;
import database.DbControlArrivalData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class ControlBehaviour extends OneShotBehaviour {

	ACLMessage msg;
	FlexibilityData msgData;
	@SuppressWarnings("unchecked")
	public ControlBehaviour(ACLMessage msg) {
		this.msg = msg;
		try {
			msgData = (FlexibilityData)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void action() {
		/**
		 * waits for messages to arrive from the 3 aggregators and make the choice 
		 * In version 2.0 I can implement a "persistent delivery of ACL messages" 
		 * 
		 *
		 * Send the message with the result that is a single vector made by 3 values: 
		 * PERIOD (sec) - POWER (kw) - COST (€) -> Result.java
		 */
		
		
		// DEVO RICEVERE TUTTI E 3 I MESSAGGI, SE NON LI RICEVO ALLORA DEVO FARE QUALCOSA PER RECUPERARLI
		// SI MA POI SI VEDE, I won't ignore :)
		
		ControlFlexibilityData controlData = new ControlFlexibilityData(this.myAgent.getName(), msgData);
		controlData.setIdAgent(this.myAgent.getName());
		new DbControlArrivalData().addControlArrivalData(controlData);

		int messagesReceived = new DbControlArrivalData().countMessagesReceived(this.myAgent.getName());
		System.out.println("messagesReceived: "+messagesReceived);
		
		if (messagesReceived == 3)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * think and tell them what to do 
			 */
			
			//FARE LA FUNZIONE GETLASTDATA -> poi vai sui der, lascia perdere qui
			ControlFlexibilityData BatteryData = new DbControlArrivalData().getControlArrivalDatabyType(this.myAgent.getName(), "battery");
			ControlFlexibilityData DerData = new DbControlArrivalData().getControlArrivalDatabyType(this.myAgent.getName(), "der");
			ControlFlexibilityData LoadData = new DbControlArrivalData().getControlArrivalDatabyType(this.myAgent.getName(), "load");

			//TO-DO how to get the prices?
			
		}
	}
	
	/**
	 * Checks if the list of what the agents decided to does not overcome the limit given as input 
	 * @return
	 */
	private boolean peakShaving()
	{
		boolean ok = false;
		return ok;
	}

}
