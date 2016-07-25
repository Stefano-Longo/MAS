package behaviours;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utils.GeneralData;
	
@SuppressWarnings("serial")
public class CalculatePrices extends OneShotBehaviour {

	/**
	 * This class takes in input the prices for the next hour and calculate 
	 * the prices for the hours after the next one estimating them
	 */
	
	private ACLMessage msg;
	int timeSlot = new GeneralData().getTimeSlot();

	public CalculatePrices(ACLMessage msg){
		this.msg = msg;
	}

	@Override
	public void action() {
		/**
		 * Elaborate and calculate the prices for all the hours of the day and put it in a list
		 * Then add the list to the message's content
		 */
		ArrayList<TimePowerPrice> list = new ArrayList<TimePowerPrice>();
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
		list.add(element);
		
		int start = cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60;
		
		for(int i = start+timeSlot; 3600*24 > i ; i+=timeSlot){
			cal.add(Calendar.SECOND, timeSlot);
			Random rn = new Random();
			/**
			 * TO-DO Here there should be the forecast!!!
			 */
			//the new costs can be from 20% to 250% of the last cost
			double energyPrice = element.getEnergyPrice()*(rn.nextInt(23)+2)/10; 
			TimePowerPrice e = new TimePowerPrice(cal.getTime(), element.getMaxEnergy(), round(energyPrice, 2));
			list.add(e);
		}

		//print list
		for(int i=0; i<list.size(); i++)
		{
			System.out.println(list.get(i).getDateTime()+" "+list.get(i).getEnergyPrice()+" "
					+ list.get(i).getMaxEnergy());
		}
		//end print list
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent", "input", list);
			
	}
	
	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
