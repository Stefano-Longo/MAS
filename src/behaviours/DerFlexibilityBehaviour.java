package behaviours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import agents.BaseAgent;
import basicData.BatteryData;
import basicData.DerData;
import basicData.DerInfo;
import basicData.FlexibilityData;
import basicData.LoadData;
import basicData.LoadInfo;
import basicData.TimePowerPrice;
import database.DbBatteryData;
import database.DbDerData;
import database.DbDerInfo;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class DerFlexibilityBehaviour extends OneShotBehaviour {

	ArrayList<TimePowerPrice> msgData = null; 

	@SuppressWarnings("unchecked")
	public DerFlexibilityBehaviour(ACLMessage msg) {
		try {
			this.msgData = (ArrayList<TimePowerPrice>)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void action() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(msgData.get(0).getDateTime());
		
		DerInfo derInfo = new DbDerInfo().getDerInfoByIdAgent(this.myAgent.getName());
		DerData derDataAvg = new DbDerData().getAverageLastMonthProduction(derInfo.getIdDer(), cal);
		
		double desideredChoice = derDataAvg.getProductionRequested();
		double lowerLimit = getLowerLimit(derInfo);
		double upperLimit = getUpperLimit(derInfo);
			
		double costKwh = getCostKwh(derInfo);
		
		FlexibilityData result = new FlexibilityData(cal, lowerLimit, upperLimit, costKwh, desideredChoice);


		DerData derData = new DerData(derInfo.getIdDer(), cal, costKwh, 
				lowerLimit, upperLimit, 0, desideredChoice);
		new DbDerData().addDerData(derData);
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
				"proposal", result);
	}
	
	private double getLowerLimit(DerInfo derInfo)
	{
		return derInfo.getProductionMax()*derInfo.getUsageMin()/100;
	}
	
	private double getUpperLimit(DerInfo derInfo)
	{
		if(derInfo.getType() == "generator")
		{
			return derInfo.getProductionMax()*derInfo.getUsageMax()/100;
		}
		else if(derInfo.getType() == "photovoltaic")
		{
			return calculatePvMaxProductionTime(derInfo.getProductionMax()); 
		}
		else if(derInfo.getType() == "hydro") //To-Do use hydro as a controllable der
		{
			return derInfo.getProductionMax()*derInfo.getUsageMax()/100;
		}
		else if(derInfo.getType() == "wind")
		{
			return derInfo.getProductionMax()*derInfo.getUsageMax()/100;
		}
		return 0;
	}

	private double calculatePvMaxProductionTime(double ProductionMax)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(msgData.get(0).getDateTime());
		
		double UpperLimit = ProductionMax;
		int hour = cal.get(Calendar.HOUR);
		
		if(hour < 6 || hour > 19)
		{
			return UpperLimit - (UpperLimit*1);
		}
		else if(hour < 10 || hour > 15)
		{
			return UpperLimit - (UpperLimit*0.4);
		}
		else
		{
			return UpperLimit;
		}
	}
	
	private double getCostKwh(DerInfo derInfo)
	{
		double costKwh = (derInfo.getCapitalCost()+derInfo.getMaintenanceCost())/derInfo.getTotalKwh();
		if(derInfo.getType() == "generator")
		{
			costKwh += new GeneralData().getDieselKwhPrice();
		}
		
		return costKwh;
	}
}
