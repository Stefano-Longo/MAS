package behaviours;

import java.text.ParseException;
import java.util.Date;

import agents.BaseAgent;
import basicData.ResultPowerPrice;
import basicData.TimePowerPrice;
import database.DbPriceData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import utils.GeneralData;

@SuppressWarnings("serial")
public class TsoBehaviour extends OneShotBehaviour {

	ResultPowerPrice msgData = new ResultPowerPrice(); 
	
	public TsoBehaviour(ACLMessage msg){
		try {
			this.msgData = (ResultPowerPrice)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	public TsoBehaviour(TimePowerPrice data)
	{
		this.msgData.setDatetime(data.getDatetime());
	}
	
	@Override
	public void action() {

		TimePowerPrice data = new DbPriceData().getNewTimePowerPrice(msgData.getDatetime());
		try {
			if(data != null && data.getDatetime().getTime().compareTo(GeneralData.getFormat().parse("2016-08-23 00:00")) != 0)
			{
				new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
						"GridAgent", "input", data);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
