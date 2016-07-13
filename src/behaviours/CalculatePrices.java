package behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
	
@SuppressWarnings("serial")
public class CalculatePrices extends OneShotBehaviour {

	/**
	 * This class takes in input the prices for the next hour and calculate 
	 * the prices for the hours after the next one estimating them
	 */
	private ACLMessage msg;
	
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
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
	    	//08/12/1992 15:00, 150, 0.35
	    	TimePowerPrice element = new TimePowerPrice();
			String[] msgs = msg.getContent().split(",");
			cal.setTime(format.parse(msgs[0]));
			element.setTime(cal);
			element.setMaxEnergy(Double.parseDouble(msgs[1]));
			element.setEnergyPrice(Double.parseDouble(msgs[2]));
			list.add(element);
			
			for(int i = cal.get(Calendar.HOUR_OF_DAY); i<24; i++){
				cal.add(Calendar.HOUR_OF_DAY, 1);
				Random rn = new Random();
				/**
				 * Here there should be the forecast!!!
				 */
				//the new cost can be from 50% to 150% of the last cost
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				double energyPrice = element.getEnergyPrice()*(rn.nextInt(10)+5)/10; 
				TimePowerPrice e = new TimePowerPrice(cal, element.getMaxEnergy(), energyPrice);
				list.add(e);
			}

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent", "input", list);
			
	    } catch (ParseException e1) {
			e1.printStackTrace();
	    }
	}
}
