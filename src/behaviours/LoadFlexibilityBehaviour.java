package behaviours;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import agents.BaseAgent;
import basicData.FlexibilityData;
import basicData.LoadData;
import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import basicData.TimePowerPrice;
import database.DbAggregatorLoad;
import database.DbLoadData;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class LoadFlexibilityBehaviour extends OneShotBehaviour {

	ArrayList<TimePowerPrice> msgData = null; 
	ACLMessage msg;

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
		/**
		 * CostKwh: For each Kwh shifted, the load agent will earn or lose CostKwh €
		 * Because it is defined like -> CostKwh = CostKwh_now - CostKwh_toDatetime
		 */
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		
		//check solution number in history
		int solutionNumber;
		if(msg.getConversationId().equals("input")){
			solutionNumber = 0;
		}else{
			System.out.println("ehy man, i'm again here");
			LoadData loadDataHistory = new DbLoadData().getLastLoadData(loadInfo.getIdLoad(), msgData.get(0).getDatetime());
			solutionNumber = loadDataHistory.getSolutionNumber()+1;
		}
		//System.out.println("solutionNumber: "+solutionNumber);
		ArrayList<LoadInfoPrice> loadInfoPrice = new DbLoadInfo().
				getLoadInfoPricebyIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		
		// I take always the first element of loadInfoPrice because is the one which has the lowest Price
		double lowerLimit = loadInfo.getCriticalConsumption() + loadInfo.getConsumptionAdded();
		double upperLimit = lowerLimit + loadInfo.getNonCriticalConsumption();
		double costKwh = 0; 
		double desiredChoice;
		
		Calendar toDatetime = Calendar.getInstance();
		//If I can shift some part of the load to another slotTime
		//System.out.println("list size: "+loadInfoPrice.size());
		//System.out.println("\n\n SOLUTIONS \n\n");
		/*for(int i=0; i<loadInfoPrice.size(); i++){
			if(i==solutionNumber)
				System.out.println("This one!!");
			System.out.println(loadInfoPrice.get(i).getToDatetime());
		}*/
		if (loadInfoPrice.size() != 0 && solutionNumber <= loadInfoPrice.size()-1)
		{
			//System.out.println("entered, size != 0 -> size: "+loadInfoPrice.size());
			//costo negativo se conviene shiftare
			costKwh =  loadInfoPrice.get(solutionNumber).getPrice() - msgData.get(0).getEnergyPrice();
			toDatetime = (Calendar)loadInfoPrice.get(solutionNumber).getToDatetime().clone();
			//TO-DO need to integrate a comfort cost do take the desidered choice
			desiredChoice = costKwh >= 0 ? upperLimit : lowerLimit;
			//System.out.println("entro con todatetime: "+toDatetime.getTime());
		}
		else
		{
			//useful for peak shaving but not convenient
			desiredChoice = upperLimit;
			toDatetime = new DbLoadInfo().getToDatetimeByIdAgent(this.myAgent.getName(), msgData.get(0).getDatetime());
		}

		costKwh = GeneralData.round(costKwh, 5);
		lowerLimit = GeneralData.round(lowerLimit, 2);
		upperLimit = GeneralData.round(upperLimit, 2);
		desiredChoice = GeneralData.round(desiredChoice, 2);

		LoadData loadData = new LoadData(loadInfo.getIdLoad(), msgData.get(0).getDatetime(), costKwh, 
				loadInfo.getCriticalConsumption(), loadInfo.getNonCriticalConsumption(),
				lowerLimit, upperLimit, 0, desiredChoice, 0, toDatetime, solutionNumber);

		/*if(loadData.getToDatetime() != null)
			System.out.println("LoadFlexib"+loadData.getIdLoad()+": datetime:"+loadData.getDatetime().getTime()+" todatetime:"+loadData.getToDatetime().getTime());
		*/
		
		if(solutionNumber == 0)
			new DbLoadData().addLoadData(loadData);
		else 
			new DbLoadData().updateLoadDataToDateTime(loadData);
		
		/*if(loadData.getToDatetime() != null)
			System.out.println("LoadFlexib"+loadData.getIdLoad()+" DOPO: datetime:"+loadData.getDatetime().getTime()+" todatetime:"+loadData.getToDatetime().getTime());
*/
		FlexibilityData result = new FlexibilityData(Integer.toString(loadInfo.getIdLoad()), 
				msgData.get(0).getDatetime(), lowerLimit, upperLimit, costKwh, desiredChoice);
	
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		list.add(result);
		ArrayList<FlexibilityData> futurelist = new DbLoadInfo().getFutureLoadInfoByIdAgent(loadInfo.getIdLoad(), msgData.get(0).getDatetime());
		//modify the future list with the shifted power
		Boolean done = false;
		//if(toDatetime!=null)System.out.println("todatetime = "+toDatetime.getTime());
		for(int i=0; i<futurelist.size() && toDatetime!=null && done==false; i++)
		{
			if(futurelist.get(i).getDatetime().getTime().equals(toDatetime.getTime()))
			{
				double consumptionShifted = upperLimit - desiredChoice;
				futurelist.get(i).setLowerLimit(futurelist.get(i).getLowerLimit() + consumptionShifted);
				futurelist.get(i).setUpperLimit(futurelist.get(i).getUpperLimit() + consumptionShifted);
				done = true;
			}
		}
		list.addAll(futurelist);
		
		
		/*DateFormat format = GeneralData.getFormat();
		for(int i=0; i < futurelist.size(); i++)
		{
			System.out.println();
			System.out.print(" "+futurelist.get(i).getIdAgent());
			System.out.print(" "+format.format(futurelist.get(i).getDatetime().getTime()));
			System.out.print(" "+futurelist.get(i).getLowerLimit());
			System.out.print(" "+futurelist.get(i).getDesiredChoice());
			System.out.print(" "+futurelist.get(i).getUpperLimit());
			System.out.println();
		}*/
		
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "LoadAggregatorAgent",
				"proposal", list);
	}
	
}
