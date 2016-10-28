package behaviours;

import java.util.ArrayList;

import agents.BaseAgent;
import basicData.FlexibilityData;
import basicData.DerInfo;
import basicData.ResultPowerPrice;
import database.DbAggregatorDer;
import database.DbDerInfo;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class DisaggregateDerBehaviour extends OneShotBehaviour{

	ACLMessage msg;
	ResultPowerPrice msgData;
	ArrayList<FlexibilityData> derChoices = new ArrayList<FlexibilityData>();
	
	public DisaggregateDerBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void action() {
		/**
		 * I ask first the maximum from solar, wind and hydro. Then I ask to generator if needed
		 */
		derChoices = new DbAggregatorDer().getDersChoice(this.myAgent.getName(), msgData.getDatetime());
		DFAgentDescription[] derAgents = new BaseAgent().getAgentsbyServiceType(myAgent, "DerAgent");
		if(derChoices.size() == derAgents.length)
		{
			takeFromMostConvenient();
		}
	}
	
	private void takeFromMostConvenient()
	{
		double totalPowerRequested = msgData.getPowerRequested();
		double derPowerRequested = 0;
		derChoices.sort((o1, o2) -> Double.compare(o1.getCostKwh(),o2.getCostKwh()));
		// the first der is the one with lower CostKwh

		for(int i=0; i < derChoices.size(); i++)
		{
			//ControlAgent sends this: datetime, power, CostKwh
			if(totalPowerRequested > derChoices.get(i).getUpperLimit())
			{
				derPowerRequested = derChoices.get(i).getUpperLimit();
				totalPowerRequested -= derChoices.get(i).getUpperLimit();

			}
			else 
			{
				derPowerRequested = totalPowerRequested;
				totalPowerRequested = 0;
			} 
			ResultPowerPrice derAction = new ResultPowerPrice(msgData.getDatetime(), derPowerRequested, msgData.getCostKwh());
			
			DerInfo derInfo = new DbDerInfo().getDerByIdDer(derChoices.get(i).getIdAgent());
			
			String shortName = new BaseAgent().getShortName(derInfo.getIdAgent());
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					myAgent.getAID(shortName), "result", derAction);
		}
	}

}
