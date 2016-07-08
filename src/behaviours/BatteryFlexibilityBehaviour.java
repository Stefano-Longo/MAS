package behaviours;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import basicData.BatteryData;
import basicData.BatteryInfo;
import basicData.FlexibilityData;
import basicData.TimePowerPrice;
import database.DbBatteryData;
import database.DbBatteryInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class BatteryFlexibilityBehaviour extends OneShotBehaviour {

	private int timeSlot = new GeneralData().timeSlot; 
	private ACLMessage msg;
	ArrayList<TimePowerPrice> msgData = null; 

	@SuppressWarnings("unchecked")
	public BatteryFlexibilityBehaviour(ACLMessage msg) {
		try {
			this.msg = msg;
			this.msgData = (ArrayList<TimePowerPrice>)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void action() {
		/**
		 * Takes in input the price for the next hours
		 * 
		 * Sends a message to the BatteryAggregatorAgent containing the energy he can give/take
		 * for the next hour depending on the electricity-price and on the flexibility-price,
		 * on the capacity and on the current soc
		 * 
		 * This has the objectiveSOC at 60% at the beginning but then he needs to update this value
		 * learning from the past. How?
		 * 
		 */
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();

		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(this.myAgent.getName());
		BatteryData batteryData = new DbBatteryData().getLastBatteryData(batteryInfo.getIdBattery());
		
		double newSocObjective = batteryData.getSocObjective(); //always 60% for now
		
		double maxInput = getMaxInput(batteryData.getSoc(), batteryInfo.getSocMax(), batteryInfo.getCapacity(), 
				batteryInfo.getBatteryInputMax());
		double maxOutput = getMaxOutput(batteryData.getSoc(), batteryInfo.getSocMin(), batteryInfo.getCapacity(), 
				batteryInfo.getBatteryOutputMax());
		
    	/**
		 * define what the battery wants to do and the flexibility and the gain it has doing that
		 * use getSocObjective, or anyway another function
		 */
		double socObjectiveDesideredChoice = calculateSocObjectiveDesideredChoice(batteryData.getSoc(), 
				batteryInfo.getSocMax(), batteryInfo.getCapacity(), batteryInfo.getBatteryInputMax(), 
				batteryInfo.getBatteryOutputMax(), newSocObjective);
		double historyDesideredChoice = calculateHistoryDesideredChoice();
		double priceDesideredChoice = calculatePriceDesideredChoice();
		double desideredChoice = (socObjectiveDesideredChoice + historyDesideredChoice + priceDesideredChoice)/3;
		//int desideredRandomChoice = (int)ThreadLocalRandom.current().nextDouble(-maxInput, maxOutput+1);
		
		
		/**
		 * PRIORITIES!! Think about it!
		 * 
		 * How to calculate desideredChoice :
		 *  - Reaching the SocObjective (33% of final value)
		 *  - Be ready for the next requests -> if the requests for the next periods usually (the majority)
		 *  	are of request of power from batteries, then I need to charge now (33% of final value)
		 *  - Depending on the prices of electricity and the price of flexibility. If it's much more convenient
		 *  	to sell now than another time during the day, so sell (33% of final value)
		 *  DesideredChoice: medium of these 3 values!!
		 *  
		 */
		
		FlexibilityData data = new FlexibilityData();
		Calendar cal = Calendar.getInstance();
		cal.setTime(batteryData.getDatetime().getTime());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.SECOND, new GeneralData().timeSlot);
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		data = new FlexibilityData(now, cal, -maxInput,
    			maxOutput, batteryData.getCostKwh(), desideredChoice);
		list.add(data);
		
		list.addAll(estimateNextHours(now));
		
		ACLMessage response = this.msg.createReply();
		response.setPerformative(ACLMessage.INFORM);
		response.setConversationId("proposal");
		try {
			response.setContentObject(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.myAgent.send(response);
    	/**
		 * Aggiungere le previsioni per le prossime ore
		 */
    	

		
	}
	
	private ArrayList<FlexibilityData> estimateNextHours (Calendar now){ // TO-DO magari chiedi a Francesco, prendi quelle di ieri
		//select from db all data where 
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		return list;
	}
	
	private double calculateSocObjectiveDesideredChoice (double soc, double socMax, double capacity,
			double maxInputBattery, double maxOutputBattery, double nextSocObjective)
	{
		if(soc < nextSocObjective)
		{
			return getMaxInput(soc, socMax, capacity, maxInputBattery);
		}
		else
		{
			return getMaxOutput(soc, socMax, capacity, maxOutputBattery);
		}
	}
	
	private double calculateHistoryDesideredChoice ()
	{
		/**
		 * Accede al db, prende i dati vecchi e vede se di solito nelle ore successive il CA chiede o dà kw
		 */
		
	}
		
	private double calculatePriceDesideredChoice ()
	{
		msgData.get(0).getEnergyPrice();
		msgData.get(0).getFlexibilityPrice();
		
	}
	
 	private double getMaxInput(double soc, double socMax, double capacity, double maxInputBattery)
	{
        if (soc >= socMax)
            return 0;
        double maxBatteryInputPercentage = (socMax - soc) * capacity * (3600/timeSlot) / 100;
        if (maxBatteryInputPercentage > maxInputBattery)
        {
        	return maxInputBattery;
        }
    	return maxBatteryInputPercentage;
    }
	
	private double getMaxOutput(double soc, double socMin, double capacity, double maxOutputBattery)
	{
        if (soc <= socMin)
            return 0;
        double maxBatteryOutputPercentage = (soc - socMin) * capacity * (3600/timeSlot) / 100;

        if (maxBatteryOutputPercentage > maxOutputBattery)
        {
        	return maxOutputBattery;
        }
    	return maxBatteryOutputPercentage;
    }
		
}
