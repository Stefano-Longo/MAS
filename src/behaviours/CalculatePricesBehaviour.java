package behaviours;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import database.DbGridData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utils.GeneralData;
	
@SuppressWarnings("serial")
public class CalculatePricesBehaviour extends OneShotBehaviour {

	/**
	 * This class takes in input the prices for the next hour and calculate 
	 * the prices for the hours after the next one estimating them
	 */
	
	private ACLMessage msg;
	int timeSlot = new GeneralData().getTimeSlot();

	public CalculatePricesBehaviour(ACLMessage msg){
		this.msg = msg;
	}

	@Override
	public void action() {
		/**
		 * Elaborate and calculate the prices for all the hours of the day and put it in a list
		 * Then add the list to the message's content
		 */
		ArrayList<TimePowerPrice> priceData = new ArrayList<TimePowerPrice>();
	    Calendar cal = Calendar.getInstance(); // creates calendar
		DateFormat format = new GeneralData().getFormat();
		
    	//08/12/1992 15:00, 150, 0.35
    	
		String[] msgs = msg.getContent().split(",");
		try {
			cal.setTime(format.parse(msgs[0]));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		TimePowerPrice element = new TimePowerPrice(cal.getTime(), Double.parseDouble(msgs[1].trim()),
				Double.parseDouble(msgs[2].trim()));
		priceData.add(element);
		
		int start = cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60;
		
		for(int i = start+timeSlot; 3600*24 > i ; i+=timeSlot){
			cal.add(Calendar.SECOND, timeSlot);
			Random rn = new Random();
			/**
			 * TO-DO Here there should be the forecast!!!
			 */
			//the new costs can be from 20% to 250% of the last cost
			double energyPrice = new GeneralData().getMeanKwhPrice()*(rn.nextInt(23)+2)/10; 
			TimePowerPrice e = new TimePowerPrice(cal.getTime(), element.getMaxEnergy(), new GeneralData().round(energyPrice, 4));
			priceData.add(e);
		}

		//print list
		for(int i=0; i<priceData.size(); i++)
		{
			System.out.println(priceData.get(i).getDateTime()+" "+priceData.get(i).getEnergyPrice()+" "
					+ priceData.get(i).getMaxEnergy());
		}
		//end print list
		
		new DbGridData().addPriceData(priceData);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent", "input", priceData);
			
	}
}
