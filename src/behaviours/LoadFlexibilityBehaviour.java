package behaviours;

import java.util.ArrayList;
import java.util.Calendar;

import agents.BaseAgent;
import basicData.FlexibilityData;
import basicData.LoadData;
import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import basicData.TimePowerPrice;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class LoadFlexibilityBehaviour extends OneShotBehaviour {

	ArrayList<TimePowerPrice> msgData = null; 

	@SuppressWarnings("unchecked")
	public LoadFlexibilityBehaviour(ACLMessage msg) {
		try {
			this.msgData = (ArrayList<TimePowerPrice>)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() 
	{
		ArrayList<LoadInfoPrice> loadInfoPrice = new DbLoadInfo().getLoadInfoPricebyIdAgent(this.myAgent.getName());
		/**
		 * Add the price to every object of the list, then order the list on the price value ASC 
		 */
		for(int i=0; i < loadInfoPrice.size(); i++)
		{
			double energyPrice = getPriceByDatetime(loadInfoPrice.get(i).getToDatetime());
			loadInfoPrice.get(i).setPrice(energyPrice);
		}
		loadInfoPrice.sort((o1, o2) -> Double.compare(o1.getPrice(),o2.getPrice()));

		// I take always the first element because is the one which have less Price
		double lowerLimit = loadInfoPrice.get(0).getCriticalConsumption() + loadInfoPrice.get(0).getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfoPrice.get(0).getNonCriticalConsumption();
		double desideredChoice;
		double costKwh = msgData.get(0).getEnergyPrice() - loadInfoPrice.get(0).getPrice(); //prezzo attuale meno prezzo futuro stimato
		
		desideredChoice = costKwh < 0 ? lowerLimit : upperLimit;

		FlexibilityData result = new FlexibilityData(msgData.get(0).getTime(), lowerLimit, upperLimit, costKwh, desideredChoice);
		
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName());
		LoadData loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getTime(), costKwh, 
				loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
				lowerLimit, upperLimit, 0, desideredChoice, 0, loadInfoPrice.get(0).getToDatetime());
		new DbLoadData().addLoadData(loadData);
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"proposal", result);
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
