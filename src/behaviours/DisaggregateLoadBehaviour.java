package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.AggregatorFlexibilityData;
import basicData.BatteryInfo;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.ResultPowerPrice;
import database.DbAggregatorBattery;
import database.DbAggregatorLoad;
import database.DbBatteryInfo;
import database.DbLoadInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class DisaggregateLoadBehaviour extends OneShotBehaviour{

	ACLMessage msg;
	ResultPowerPrice msgData;
	ArrayList<AggregatorFlexibilityData> loadsChoice = new DbAggregatorLoad()
			.getLoadsChoice(this.myAgent.getName());
	
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
		DFAgentDescription[] loadAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "LoadAgent");
		
		FlexibilityData loadAggregatedData = new DbAggregatorLoad().aggregateMessageReceived(this.myAgent.getName());
		if(loadsChoice.size() == loadAgents.length)
		{
			if(msgData.getPowerRequested() > loadAggregatedData.getDesideredChoice())
			{
				/**
				 * Garantisco a tutti la loro scelta (desideredChoice) e il surplus lo d� 
				 * a chi mi garantisce un prezzo basso per ogni Kwh spostato
				 */
				double residualPower = msgData.getPowerRequested() - loadAggregatedData.getDesideredChoice();
				giveDesideredChoicePlusResidual(residualPower);
			}
			else if(msgData.getPowerRequested() < loadAggregatedData.getDesideredChoice())
			{
				/**
				 * Garantisco a tutti il minimo (lowerLimit) e il surplus lo d� 
				 * a chi mi garantisce un prezzo basso per ogni Kwh spostato (A CHI HA UN PREZZO NEGATIVO!!)
				 */
				double residualPower = msgData.getPowerRequested() - loadAggregatedData.getLowerLimit();
				giveMinimumPlusResidual(residualPower);
			}
			else if(msgData.getPowerRequested() == 0)
			{
				switchOff();
			}
		}
		
	}
	

	private void giveDesideredChoicePlusResidual(double residualPowerRequested)
	{
		double loadPowerRequested;
		for(int i=0; i < loadsChoice.size(); i++)
		{
			if(residualPowerRequested + loadsChoice.get(i).getDesideredChoice() > loadsChoice.get(i).getUpperLimit())
			{
				loadPowerRequested = loadsChoice.get(i).getUpperLimit();
				residualPowerRequested -= loadsChoice.get(i).getUpperLimit()-loadsChoice.get(i).getDesideredChoice();
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
					loadPowerRequested, msgData.getCostKwh());

			LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(loadsChoice.get(i).getIdentificator(), msgData.getDatetime());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					loadInfo.getIdAgent(), "response", loadAction);
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
					loadPowerRequested, msgData.getCostKwh());

			LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(loadsChoice.get(i).getIdentificator(), msgData.getDatetime());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					loadInfo.getIdAgent(), "response", loadAction);
		}
	}
	
	private void switchOff()
	{
		for(int i=0; i < loadsChoice.size(); i++)
		{
			ResultPowerPrice loadAction = new ResultPowerPrice(msgData.getDatetime(), 0, msgData.getCostKwh());

			LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(loadsChoice.get(i).getIdentificator(), msgData.getDatetime());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					loadInfo.getIdAgent(), "response", loadAction);
		}
	}
	
}
