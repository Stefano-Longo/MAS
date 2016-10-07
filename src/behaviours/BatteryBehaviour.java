package behaviours;

import agents.BaseAgent;
import basicData.BatteryData;
import basicData.BatteryInfo;
import basicData.OkData;
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
		
		BatteryInfo batteryInfo = new DbBatteryInfo().getBatteryInfoByIdAgent(this.myAgent.getName());
		BatteryData lastBatteryData = new DbBatteryData().getBatteryDataByDatetime(batteryInfo.getIdBattery(), msgData.getDatetime());

		//BatteryOutput -> powerRequested negative value
		//BatteryInput -> powerRequested positive value
		if((msgData.getPowerRequested() > 0 && msgData.getPowerRequested() > lastBatteryData.getInputPowerMax()) || 
				(msgData.getPowerRequested() < 0 && msgData.getPowerRequested() < lastBatteryData.getOutputPowerMax()))
		{
			System.out.println("I'm the battery "+batteryInfo.getIdBattery()+" and I don't accept this power requested: "
				+ msgData.getPowerRequested()+ " because my limist are: "+lastBatteryData.getInputPowerMax()+" "+lastBatteryData.getOutputPowerMax()
				+ "\nfor datetime: "+msgData.getDatetime().getTime());
			OkData ko = new OkData(msgData.getDatetime(), "battery", false);
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "BatteryAggregatorAgent",
					"ok", ko);
			return;
		}
		
		double newSoc = calculateSoc(lastBatteryData.getSoc(), batteryInfo.getCapacity(), msgData.getPowerRequested());
		double newSocObjective = lastBatteryData.getSocObjective();
		
		//TO-DO per ora newSocObjective sempre uguale. Poi vediamo se è meglio che si aggiorni per ogni ora
		
		double newCostKwh = calculateNewCostKwh(lastBatteryData.getSoc(), lastBatteryData.getCapacity(), lastBatteryData.getCostKwh());
		newCostKwh = GeneralData.round(newCostKwh, 5);
		
		BatteryData batteryData = new BatteryData(batteryInfo.getIdBattery(), lastBatteryData.getDatetime(), 
				newSocObjective, newSoc, newCostKwh, msgData.getPowerRequested());
		new DbBatteryData().updateBatteryData(batteryData); //salvo nello storico
		
		OkData ok = new OkData(msgData.getDatetime(), "battery", true);
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "BatteryAggregatorAgent",
				"ok", ok);
	}

	private double calculateSoc(double soc, double capacity, double powerRequested)
	{
		double newSoc = soc + ((powerRequested / (3600 / timeSlot))*100 / capacity);
		return GeneralData.round(newSoc, 2);
	}
	
	private double calculateNewCostKwh(double soc, double capacity, double oldCostKwh)
	{
		if(msgData.getPowerRequested() <= 0) // output energy -> the cost does not change
		{
			return oldCostKwh;
		}
		BatteryInfo bInfo = new DbBatteryInfo().getBatteryInfoByIdAgent(this.myAgent.getName());
		double cycleCost = (bInfo.getCapitalCost() + bInfo.getMaintenanceCost())/bInfo.getCyclesNumber();
		double costNewKwh = (cycleCost/(2*bInfo.getCapacity()))+((1-bInfo.getRoundTripEfficiency())*msgData.getCostKwh());
		double newKwh = msgData.getPowerRequested()/(3600/timeSlot);
		double oldKwh = soc*capacity;
		double newCostKwh = (oldCostKwh*(oldKwh) + costNewKwh*(newKwh))/(oldKwh+newKwh);
		
		return newCostKwh;
	}
}
