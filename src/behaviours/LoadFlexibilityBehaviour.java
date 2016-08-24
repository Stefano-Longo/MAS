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
		/*Calendar cal = Calendar.getInstance();
		cal.setTime(msgData.get(0).getDateTime());*/
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), msgData.get(0).getDateTime());
		ArrayList<LoadInfoPrice> loadInfoPrice = new DbLoadInfo().
				getLoadInfoPricebyIdAgent(this.myAgent.getName(), msgData.get(0).getDateTime());
		
		// I take always the first element because is the one which has the lower Price
		double lowerLimit = loadInfo.getCriticalConsumption() + loadInfo.getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfo.getNonCriticalConsumption();
		double desideredChoice;
		double costKwh = msgData.get(0).getEnergyPrice(); //prezzo attuale meno prezzo futuro stimato
		costKwh = GeneralData.round(costKwh, 2);
		
		desideredChoice = costKwh < 0 ? lowerLimit : upperLimit;

		lowerLimit = GeneralData.round(lowerLimit, 2);
		upperLimit = GeneralData.round(upperLimit, 2);
		desideredChoice = GeneralData.round(desideredChoice, 2);
		
		LoadData loadData = new LoadData();
		//If I can shift some part of the load to another slotTime
		if (loadInfoPrice.size() > 0)
		{
			/**
			 * Add the price to every object of the list, then order the list on the price value ASC 
			 */
			for(int i=0; i < loadInfoPrice.size(); i++)
			{
				double energyPrice = getPriceByDatetime(loadInfoPrice.get(i).getToDatetime());
				loadInfoPrice.get(i).setPrice(energyPrice);
			}
			loadInfoPrice.sort((o1, o2) -> Double.compare(o1.getPrice(),o2.getPrice()));
			costKwh = msgData.get(0).getEnergyPrice() - loadInfoPrice.get(0).getPrice();
			loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getDateTime(), costKwh, 
					loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
					lowerLimit, upperLimit, 0, desideredChoice, 0, loadInfoPrice.get(0).getToDatetime());
		}
		else
		{
			loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getDateTime(), costKwh, 
					loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
					lowerLimit, upperLimit, 0, desideredChoice, 0, null);
		}
		
		FlexibilityData result = new FlexibilityData(msgData.get(0).getDateTime(), lowerLimit, upperLimit, 
				costKwh, desideredChoice, "load");
		System.out.println(this.myAgent.getName());
		System.out.println(loadData.getToDatetime());
		System.out.println(loadData.getDatetime().getTime());
		new DbLoadData().addLoadData(loadData);
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"proposal", result);
	}
	
	
	private double getPriceByDatetime(Calendar datetime)
	{
		for(int i=0; i < msgData.size(); i++)
		{
			if(msgData.get(i).getDateTime().getTime().compareTo(datetime.getTime()) == 0)
			{
				return msgData.get(i).getEnergyPrice();
			}
		}
		return 0;
	}
	
}
