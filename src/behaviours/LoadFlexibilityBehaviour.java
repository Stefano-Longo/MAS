package behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import basicData.TimePowerPrice;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class LoadFlexibilityBehaviour extends OneShotBehaviour {

	private int timeSlot = new GeneralData().timeSlot; 
	private ACLMessage msg;
	ArrayList<TimePowerPrice> msgData = null; 

	@SuppressWarnings("unchecked")
	public LoadFlexibilityBehaviour(ACLMessage msg) {
		try {
			this.msg = msg;
			this.msgData = (ArrayList<TimePowerPrice>)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() 
	{
		ArrayList<LoadInfoPrice> loadInfo = new DbLoadInfo().getLoadInfoPricebyIdAgent(this.myAgent.getName());
		/**
		 * Add the price to every object of the list, then order the list on the price value ASC 
		 */
		for(int i=0; i < loadInfo.size(); i++)
		{
			double energyPrice = getPriceByDatetime(loadInfo.get(i).getDatetime());
			loadInfo.get(i).setPrice(energyPrice);;
		}
		loadInfo.sort((o1, o2) -> Double.compare(o1.getPrice(),o2.getPrice()));

		
		Calendar cal = Calendar.getInstance();
		cal.setTime(msgData.get(0).getTime().getTime());
		
		double lowerLimit = loadInfo.get(0).getCriticalConsumption() + loadInfo.get(0).getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfo.get(0).getNonCriticalConsumption();
		double desideredChoice = upperLimit;
		double costKwh = msgData.get(0).getEnergyPrice() - loadInfo.get(0).getPrice(); //prezzo attuale meno prezzo futuro stimato
		
		FlexibilityData data = new FlexibilityData(cal, lowerLimit, upperLimit, costKwh, desideredChoice);
		
		ACLMessage response = this.msg.createReply();
		response.setPerformative(ACLMessage.INFORM);
		response.setConversationId("proposal");
		try {
			response.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.myAgent.send(response);
	}
	
	private double getPriceByDatetime(Calendar datetime)
	{
		for(int i=0; i < msgData.size(); i++)
		{
			if(msgData.get(i).getTime().equals(datetime))
				return msgData.get(i).getEnergyPrice();
		}
		return 0;
	}
	
}
