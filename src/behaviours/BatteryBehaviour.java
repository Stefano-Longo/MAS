package behaviours;

import java.util.Calendar;

import basicData.BatteryData;
import basicData.BatteryInfo;
import basicData.ResultData;
import database.DbBatteryData;
import database.DbBatteryInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

public class BatteryBehaviour  extends OneShotBehaviour {

	/**
	 * Apply the values given by the aggregator: save them into the database
	 */
	private static final long serialVersionUID = 1L;

	ACLMessage msg;
	ResultData msgData;
	private int timeSlot = new GeneralData().timeSlot;
	
	public BatteryBehaviour(ACLMessage msg) {
		this.msg = msg;
		try {
			msgData = (ResultData)msg.getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(this.myAgent.getName());
		BatteryData batteryData = new DbBatteryData().getLastBatteryData(batteryInfo.getIdBattery());
		
		/**
		 * Create BatteryData which has:
		 * 	- DateTime incremented by timeSlot (writing the future state, after the timeSlot)
		 * 	- Soc: the old one reduced/incresed depending on the msgData value 
		 *  - SocObjective: a medium between the old one and the values for the same hours of the last week value
		 *  - CostKwh: Calculate it 
		 *  - InputPowerMax: I should have it
		 *  - OutputPowerMax: I should have it also
		 */
		
		
		//the row in db is created before, now it is only updated with the new values
		
		/*msgData.getPower()
		msgData.getCost()
		*/
		System.out.println(batteryData.getDatetime().getTime());
		batteryData.getDatetime().add(Calendar.SECOND, timeSlot);
		System.out.println(batteryData.getDatetime().getTime());
		
		double inputPowerMax = getMaxInput(batteryData.getSoc(), batteryInfo.getSocMax(), 
				batteryInfo.getCapacity(), batteryInfo.getBatteryInputMax());
		double outputPowerMax = getMaxOutput(batteryData.getSoc(), batteryInfo.getSocMin(), 
				batteryInfo.getCapacity(), batteryInfo.getBatteryOutputMax());
		
		
		//calculate next socObjective
		double newSoc = calculateSoc(batteryData.getSoc(), batteryData.getCapacity(), msgData.getPowerRequested());
		/*BatteryData newBatteryData = new BatteryData(batteryInfo.getIdBattery(), batteryData.getDatetime(), 
				socObjective, newSoc, costKwh, inputPowerMax, outputPowerMax, msgData.getPowerRequested());
		new DbBatteryData().addBatteryData(newBatteryData);*/

	}
	
	private double getMaxInput(double soc, double socMax, double capacity, double maxInputBattery)
	{
        if (soc >= socMax)
            return 0;
        double maxBatteryInputPercentage = (socMax - soc) * capacity * (60/timeSlot) / 100;
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
        double maxBatteryOutputPercentage = (soc - socMin) * capacity * (60 / timeSlot) / 100;

        if (maxBatteryOutputPercentage > maxOutputBattery)
        {
        	return maxOutputBattery;
        }
    	return maxBatteryOutputPercentage;
    }

	private double calculateSoc(double soc, double capacity, double powerRequested)
	{
		//BatteryInput -> powerRequested negative value
		//BatteryOutput -> powerRequested positive value
		 double newSoc = soc + (int)((powerRequested / (3600 / timeSlot))*100 / capacity);

		return newSoc;
	}
}
