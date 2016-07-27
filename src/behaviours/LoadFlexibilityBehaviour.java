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
import utils.GeneralData;

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
		Calendar cal = Calendar.getInstance();
		cal.setTime(msgData.get(0).getDateTime());
		ArrayList<LoadInfoPrice> loadInfoPrice = new DbLoadInfo().getLoadInfoPricebyIdAgent(this.myAgent.getName(), cal);
		/**
		 * Add the price to every object of the list, then order the list on the price value ASC 
		 */
		for(int i=0; i < loadInfoPrice.size(); i++)
		{
			double energyPrice = getPriceByDatetime(loadInfoPrice.get(i).getToDatetime());
			loadInfoPrice.get(i).setPrice(energyPrice);
		}
		loadInfoPrice.sort((o1, o2) -> Double.compare(o1.getPrice(),o2.getPrice()));
		
		// I take always the first element because is the one which has the lower Price
		double lowerLimit = loadInfoPrice.get(0).getCriticalConsumption() + loadInfoPrice.get(0).getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfoPrice.get(0).getNonCriticalConsumption();
		double desideredChoice;
		double costKwh = msgData.get(0).getEnergyPrice() - loadInfoPrice.get(0).getPrice(); //prezzo attuale meno prezzo futuro stimato
		costKwh = new GeneralData().round(costKwh, 2);
		
		desideredChoice = costKwh < 0 ? lowerLimit : upperLimit;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(msgData.get(0).getDateTime());
		FlexibilityData result = new FlexibilityData(calendar, lowerLimit, upperLimit, 
				costKwh, desideredChoice, "load");
		
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), calendar);
		
		LoadData loadData = new LoadData(loadInfo.getIdLoad(), calendar, costKwh, 
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
			if(msgData.get(i).getDateTime().compareTo(datetime.getTime()) == 0)
			{
				return msgData.get(i).getEnergyPrice();
			}
		}
		return 0;
	}
	
}
