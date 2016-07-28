package behaviours;

import basicData.ResultPowerPrice;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class GiveOutput extends OneShotBehaviour {

	ResultPowerPrice msgData;
	public GiveOutput(ACLMessage msg) {
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {

		int slotTime = new GeneralData().getTimeSlot();
		System.out.println("\n Hi! I am the Grid Agent: "+this.myAgent.getName());
		System.out.println("The energy requested to the grid for the next "+slotTime/60+" minutes is: "+msgData.getPowerRequested()+" Kw");
		
	}

}
