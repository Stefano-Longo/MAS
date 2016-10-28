package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import database.DbPriceData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;
	
@SuppressWarnings("serial")
public class CalculatePricesBehaviour extends OneShotBehaviour {

	/**
	 * This class takes in input the prices for the next hour and calculate 
	 * the prices for the hours after the next one estimating them
	 */
	
	int timeSlot = new GeneralData().getTimeSlot();
	TimePowerPrice msgData = null; 

	public CalculatePricesBehaviour(ACLMessage msg){
		try {
			this.msgData = (TimePowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		/**
		 * Elaborate and calculate the prices for all the hours of the day and put it in a list
		 * Then add the list to the message's content
		 */
		ArrayList<TimePowerPrice> priceData = new DbPriceData().getDailyTimePowerPrice(msgData.getDatetime()) ;
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent", "input", priceData);
			
		//print list
		System.out.println("\n\n________________________________________________________________\n\n");
		for(int i=0; i<priceData.size(); i++)
		{
			System.out.println(priceData.get(i).getDatetime().getTime()+" "+priceData.get(i).getThreshold()+
					" "+priceData.get(i).getEnergyPrice()+" ");
		}
		//end print list
	}
}
