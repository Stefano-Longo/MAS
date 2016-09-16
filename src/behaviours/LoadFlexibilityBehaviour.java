package behaviours;

import java.awt.Toolkit;
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
		/**
		 * CostKwh: For each Kwh shifted, the load agent will earn or lose CostKwh €
		 * Because it is defined like -> CostKwh = CostKwh_now - CostKwh_toDatetime
		 */
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		LoadInfoPrice loadInfoPrice = new DbLoadInfo().
				getLoadInfoPricebyIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		
		// I take always the first element because is the one which has the lowest Price
		double lowerLimit = loadInfo.getCriticalConsumption() + loadInfo.getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfo.getNonCriticalConsumption();
		double costKwh = 0; 
		double desideredChoice;
		
		Calendar toDatetime = Calendar.getInstance();
		//If I can shift some part of the load to another slotTime
		if (loadInfoPrice != null)
		{
			costKwh =  loadInfoPrice.getPrice() - msgData.get(0).getEnergyPrice();
			toDatetime = (Calendar)loadInfoPrice.getToDatetime().clone();
			//TO-DO need to integrate a comfort cost do take the desidered choice
			desideredChoice = costKwh >= 0 ? upperLimit : lowerLimit;
			if(loadInfoPrice.getDatetime().getTime().compareTo(loadInfoPrice.getToDatetime().getTime()) == 0){
				System.out.println("\nALEEERT \nALEEEERT \nALEEEERT");
				return;
			}
		}
		else
		{
			desideredChoice = upperLimit;
			toDatetime = null;
		}

		costKwh = GeneralData.round(costKwh, 5);
		lowerLimit = GeneralData.round(lowerLimit, 2);
		upperLimit = GeneralData.round(upperLimit, 2);
		desideredChoice = GeneralData.round(desideredChoice, 2);

		//To-Do correggere datetime ogni tanto è uguale a toDateTime.
		LoadData loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getDatetime(), costKwh, 
				loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
				lowerLimit, upperLimit, 0, desideredChoice, 0, toDatetime);

		new DbLoadData().addLoadData(loadData);
		FlexibilityData result = new FlexibilityData(msgData.get(0).getDatetime(), lowerLimit, upperLimit, 
				costKwh, desideredChoice, "load");
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"proposal", result);
	}
	
}
