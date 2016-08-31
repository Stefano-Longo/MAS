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
		/**
		 * CostKwh: For each Kwh shifted, the load agent will earn or lose CostKwh €
		 * Because it is defined like -> CostKwh = CostKwh_now - CostKwh_toDatetime
		 */
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		ArrayList<LoadInfoPrice> loadInfoPrice = new DbLoadInfo().
				getLoadInfoPricebyIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		
		// I take always the first element because is the one which has the lowest Price
		double lowerLimit = loadInfo.getCriticalConsumption() + loadInfo.getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfo.getNonCriticalConsumption();
		double costKwh = 0; 
		double desideredChoice;
		
		Calendar toDatetime = Calendar.getInstance();
		//If I can shift some part of the load to another slotTime
		if (loadInfoPrice.size() > 0)
		{
			costKwh =  loadInfoPrice.get(0).getPrice() - msgData.get(0).getEnergyPrice();
			toDatetime = (Calendar)loadInfoPrice.get(0).getToDatetime().clone();
			//TO-DO need to integrate a comfort cost do take the desidered choice
			desideredChoice = costKwh >= 0 ? upperLimit : lowerLimit;
			System.out.println("hi, I'm the load "+loadInfo.getIdLoad()+" and my toDatetime is: "+loadInfoPrice.get(0).getToDatetime().getTime());
			if(loadInfoPrice.get(0).getDatetime() == loadInfoPrice.get(0).getToDatetime()){
				System.out.println("\nALEEERT \nALEEEERT \nALEEEERT");
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

		//To-Do correggere datetime ogni tanto è uguale a toDateTime. Per prevenire shifting in questi casi 
		// metto il costo alle stelle (basterebbe metterlo positivo o nullo).
		LoadData loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getDatetime(), costKwh, 
				loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
				lowerLimit, upperLimit, 0, desideredChoice, 0, toDatetime);
		if(loadData.getToDatetime() != null)
		{
			System.out.println("msgData.get(0).getDatetime(): "+msgData.get(0).getDatetime().getTime()+" toDatetime: "+toDatetime);
			System.out.println("loadData.getDatetime(): "+loadData.getDatetime().getTime()+" toDatetime: "+loadData.getToDatetime().getTime());
			while(loadData.getDatetime().getTime().compareTo(loadData.getToDatetime().getTime()) == 0)
			{
				System.out.println("\n\n CORREGGO COSTKWH \n\n");
				loadData.setCostKwh(1);
				loadData.setToDatetime((Calendar)loadInfoPrice.get(0).getToDatetime().clone());
			}
		}
			
		FlexibilityData result = new FlexibilityData(msgData.get(0).getDatetime(), lowerLimit, upperLimit, 
				costKwh, desideredChoice, "load");
		new DbLoadData().addLoadData(loadData);
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"proposal", result);
	}
	
}
