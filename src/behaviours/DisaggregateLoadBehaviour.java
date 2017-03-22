package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.ResultPowerPrice;
import database.DbAggregatorLoad;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class DisaggregateLoadBehaviour extends OneShotBehaviour{

	ACLMessage msg;
	ResultPowerPrice msgData;
	ArrayList<FlexibilityData> loadsChoice = new ArrayList<FlexibilityData>();
	
	public DisaggregateLoadBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() 
	{
		loadsChoice = new DbAggregatorLoad().getLoadsChoice(this.myAgent.getName(), msgData.getDatetime());
		DFAgentDescription[] loadAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "LoadAgent");
		
		FlexibilityData loadAggregatedData = new DbAggregatorLoad().aggregateMessagesReceived(this.myAgent.getName(), msgData.getDatetime());
		
		if(msgData.getPowerRequested() == 0)
		{
			switchOff();
		}
		else if(msgData.getPowerRequested() >= loadAggregatedData.getDesiredChoice())
		{
			/**
			 * Garantisco a tutti la loro scelta (desiredChoice) e il surplus lo dò 
			 * a chi mi garantisce un prezzo basso per ogni Kwh spostato
			 */
			double residualPower = msgData.getPowerRequested() - loadAggregatedData.getDesiredChoice();
			/*System.out.println("\n\nDisaggrego!! \nLowerLimit: "+loadAggregatedData.getLowerLimit()+" "
					+ "UpperLimit: "+loadAggregatedData.getUpperLimit()+" "
					+ "PowRequested: "+msgData.getPowerRequested()+" "
					+ "DesiredChoice: "+loadAggregatedData.getDesiredChoice()+" residual: "+residualPower+"\n");
			*/
			giveDesiredChoicePlusResidual(residualPower);
		}
		else if(msgData.getPowerRequested() < loadAggregatedData.getDesiredChoice())
		{
			/**
			 * Garantisco a tutti il minimo (lowerLimit) e il surplus lo dò 
			 * a chi mi garantisce un prezzo basso per ogni Kwh spostato (A CHI HA UN PREZZO NEGATIVO!!)
			 */
			double residualPower = msgData.getPowerRequested() - loadAggregatedData.getLowerLimit();
			giveMinimumPlusResidual(residualPower);
		}
			
	}
	
	private void giveDesiredChoicePlusResidual(double residualPowerRequested)
	{
		double loadPowerRequested;
		for(int i=0; i < loadsChoice.size(); i++)
		{
			if(residualPowerRequested + loadsChoice.get(i).getDesiredChoice() >= loadsChoice.get(i).getUpperLimit())
			{
				loadPowerRequested = loadsChoice.get(i).getUpperLimit();
				residualPowerRequested -= loadsChoice.get(i).getUpperLimit() - loadsChoice.get(i).getDesiredChoice();
			}
			else if (residualPowerRequested > 0)
			{
				loadPowerRequested = loadsChoice.get(i).getLowerLimit() + residualPowerRequested;
				residualPowerRequested = 0;
			}
			else //if residualPowerRequested==0
			{
				loadPowerRequested = loadsChoice.get(i).getLowerLimit();
			} 
			/*System.out.println("Load-"+loadsChoice.get(i).getIdAgent()+" LL: "+loadsChoice.get(i).getLowerLimit()
					+" UL: "+loadsChoice.get(i).getUpperLimit()+" DC: "+loadsChoice.get(i).getDesiredChoice()
					+" loadPower Requested: "+loadPowerRequested);*/
			ResultPowerPrice loadAction = new ResultPowerPrice(msgData.getDatetime(), 
					GeneralData.round(loadPowerRequested, 2), msgData.getCostKwh());
			sendMessage(loadAction, i);
		}
	}

	private void giveMinimumPlusResidual(double residualPowerRequested)
	{
		double loadPowerRequested;
		for(int i=0; i < loadsChoice.size(); i++)
		{
			if(residualPowerRequested + loadsChoice.get(i).getLowerLimit() > loadsChoice.get(i).getUpperLimit())
			{
				loadPowerRequested = loadsChoice.get(i).getUpperLimit();
				residualPowerRequested -= loadsChoice.get(i).getUpperLimit()-loadsChoice.get(i).getLowerLimit();
			}
			else if (residualPowerRequested > 0)
			{
				loadPowerRequested = loadsChoice.get(i).getLowerLimit() + residualPowerRequested;
				residualPowerRequested = 0;
			}
			else //if residualPowerRequested==0
			{
				loadPowerRequested = loadsChoice.get(i).getLowerLimit();
			} 
			ResultPowerPrice loadAction = new ResultPowerPrice(msgData.getDatetime(), 
					GeneralData.round(loadPowerRequested, 2), msgData.getCostKwh());
			sendMessage(loadAction, i);
		}
	}
	
	private void switchOff()
	{
		for(int i=0; i < loadsChoice.size(); i++)
		{
			ResultPowerPrice loadAction = new ResultPowerPrice(msgData.getDatetime(), 0, msgData.getCostKwh());
			sendMessage(loadAction, i);
		}
	}
	
	private void sendMessage(ResultPowerPrice loadAction, int counter)
	{
		LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(loadsChoice.get(counter).getIdAgent(), msgData.getDatetime());
		String shortName = new BaseAgent().getShortName(loadInfo.getIdAgent());
		new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
				this.myAgent.getAID(shortName), "result", loadAction);
	}
}
