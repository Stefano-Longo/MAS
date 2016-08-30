package behaviours;


import java.util.ArrayList;

import agents.BaseAgent;
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

	private int timeSlot = new GeneralData().getTimeSlot(); 
	ArrayList<TimePowerPrice> msgData = null; 

	@SuppressWarnings("unchecked")
	public BatteryFlexibilityBehaviour(ACLMessage msg) {
		try {
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
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryInfoByIdAgent(this.myAgent.getName());
		BatteryData batteryData = new DbBatteryData().getLastBatteryData(batteryInfo.getIdBattery());
		
		double newSocObjective = batteryData.getSocObjective(); //always 60% for now
		
		double maxInput = getMaxInput(batteryData.getSoc(), batteryInfo.getSocMax(), 
				batteryInfo.getCapacity(), batteryInfo.getBatteryInputMax());
		double maxOutput = getMaxOutput(batteryData.getSoc(), batteryInfo.getSocMin(), 
				batteryInfo.getCapacity(), batteryInfo.getBatteryOutputMax());
		
    	/**
		 * define what the battery wants to do and the flexibility and the gain it has doing that
		 * use getSocObjective, or anyway another function
		 */
		double socObjectiveDesideredChoice = calculateSocObjectiveDesideredChoice(batteryData.getSoc(), 
				batteryInfo.getSocMax(), batteryInfo.getSocMin(), batteryInfo.getCapacity(), batteryInfo.getBatteryInputMax(), 
				batteryInfo.getBatteryOutputMax(), newSocObjective);
		double desideredChoice = socObjectiveDesideredChoice;
		
		maxInput = GeneralData.round(maxInput, 2);
		maxOutput = GeneralData.round(maxOutput, 2);
		desideredChoice = GeneralData.round(desideredChoice, 2);

		FlexibilityData result = new FlexibilityData(msgData.get(0).getDateTime(), maxInput,
    			maxOutput, batteryData.getCostKwh(), desideredChoice, "battery");
		
		BatteryData data = new BatteryData(batteryInfo.getIdBattery(), msgData.get(0).getDateTime(), 
				batteryData.getSocObjective(), batteryData.getSoc(), batteryData.getCostKwh(), 
				maxInput, maxOutput, 0, desideredChoice);
		
		new DbBatteryData().addBatteryData(data);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "BatteryAggregatorAgent",
				"proposal", result);
	}
	
	private double calculateSocObjectiveDesideredChoice (double soc, double socMax, double socMin, double capacity,
			double maxInputBattery, double maxOutputBattery, double nextSocObjective)
	{
		if(soc < nextSocObjective)
		{
			return getMaxInput(soc, nextSocObjective, capacity, maxInputBattery);
		}
		else
		{
			return getMaxOutput(soc, nextSocObjective, capacity, maxOutputBattery);
		}
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
        	return -maxOutputBattery;
        }
    	return -maxBatteryOutputPercentage;
    }
		
}
