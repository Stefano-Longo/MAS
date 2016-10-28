package behaviours;

import java.util.ArrayList;
import java.util.Calendar;

import agents.BaseAgent;
import basicData.DerData;
import basicData.DerInfo;
import basicData.FlexibilityData;
import basicData.TimePowerPrice;
import database.DbDerData;
import database.DbDerInfo;
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

		DerInfo derInfo = new DbDerInfo().getDerInfoByIdAgent(this.myAgent.getName());
		DerData derDataAvg = new DbDerData().getAverageLastMonthProductionByIdDer(derInfo.getIdDer(), msgData.get(0).getDatetime());
		
		double desideredChoice = GeneralData.round(derDataAvg.getProductionRequested(),2);
		double lowerLimit = GeneralData.round(getLowerLimit(derInfo),2);
		double upperLimit = GeneralData.round(derDataAvg.getProductionRequested(),2);
		
		//System.out.println("Der"+derInfo.getIdDer()+" lowerLimit:"+lowerLimit+" upperLimit:"+upperLimit);
		double costKwh = getCostKwh(derInfo);

		FlexibilityData result = new FlexibilityData(Integer.toString(derInfo.getIdDer()), msgData.get(0).getDatetime(), 
				lowerLimit, upperLimit, costKwh, desideredChoice);
		DerData derData = new DerData(derInfo.getIdDer(), msgData.get(0).getDatetime(), costKwh, 
				lowerLimit, upperLimit, 0, desideredChoice);
		new DbDerData().addDerData(derData);
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "DerAggregatorAgent",
				"proposal", result);
	}
	
	private double getLowerLimit(DerInfo derInfo)
	{
		return derInfo.getProductionMax()*derInfo.getUsageMin()/100;
	}
	
	private double getCostKwh(DerInfo derInfo)
	{
		double costKwh = (derInfo.getCapitalCost()+derInfo.getMaintenanceCost())/derInfo.getTotalKwh();
		if(derInfo.getType() == "generator")
		{
			costKwh += new GeneralData().getDieselKwhPrice();
		}
		return GeneralData.round(costKwh, 5);
	}
}
