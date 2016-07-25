package behaviours;

import basicData.BatteryData;
import basicData.BatteryInfo;
import basicData.ResultPowerPrice;
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
	ResultPowerPrice msgData;
	private int timeSlot = new GeneralData().getTimeSlot();
	
	public BatteryBehaviour(ACLMessage msg) {
		this.msg = msg;
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		
		/**
		 * Create BatteryData which has:
		 * 	- DateTime incremented by timeSlot (writing the future state, after the timeSlot)
		 * 	- Soc: the old one reduced/incresed depending on the msgData value 
		 *  - SocObjective: a medium between the old one and the values for the same hours of the last week value
		 *  - CostKwh: Calculate it 
		 *  - InputPowerMax: I should have it
		 *  - OutputPowerMax: I should have it also
		 */
		
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryByIdAgent(this.myAgent.getName());
		//get the data to update
		BatteryData lastBatteryData = new DbBatteryData().getLastBatteryData(batteryInfo.getIdBattery());
		
		double newSoc = calculateSoc(lastBatteryData.getSoc(), lastBatteryData.getCapacity(), msgData.getPowerRequested());
		double newSocObjective = lastBatteryData.getSocObjective();
		
		//TO-DO per ora newSocObjective sempre uguale. Poi vediamo se è meglio che si aggiorni per ogni ora
		
		double newCostKwh = calculateNewCostKwh(lastBatteryData.getSoc(), lastBatteryData.getCapacity(), lastBatteryData.getCostKwh());
		
		BatteryData batteryData = new BatteryData(batteryInfo.getIdBattery(), lastBatteryData.getDatetime(), 
				newSocObjective, newSoc, newCostKwh, msgData.getPowerRequested());
		new DbBatteryData().updateBatteryData(batteryData); //salvo nello storico
	}

	private double calculateSoc(double soc, double capacity, double powerRequested)
	{
		//BatteryInput -> powerRequested negative value
		//BatteryOutput -> powerRequested positive value
		 double newSoc = soc - (int)((powerRequested / (3600 / timeSlot))*100 / capacity);

		return newSoc;
	}
	
	private double calculateNewCostKwh(double soc, double capacity, double oldCostKwh)
	{
		if(msgData.getPowerRequested() > 0) // output energy -> the cost does not change
		{
			return oldCostKwh;
		}
		BatteryInfo bInfo = new DbBatteryInfo().getBatteryByIdAgent(this.myAgent.getName());
		double cycleCost = (bInfo.getCapitalCost() + bInfo.getMaintenanceCost())/bInfo.getCyclesNumber();
		double costNewKwh = (cycleCost/(2*bInfo.getCapacity()))+((1-bInfo.getRoundTripEfficiency())*msgData.getCostKwh());
		//TO-DO control Agent sends the price expressed in costKwh!!
		double newKwh = msgData.getPowerRequested()/(3600/timeSlot);
		double oldKwh = soc*capacity;
		double newCostKwh = (oldCostKwh*(oldKwh) + costNewKwh*(newKwh))/(oldKwh+newKwh);
		
		return newCostKwh;
	}
}
