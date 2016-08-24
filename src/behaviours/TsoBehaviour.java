package behaviours;

import java.util.Calendar;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import database.DbTimePowerPrice;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class TsoBehaviour extends Behaviour {

	Calendar datetime = Calendar.getInstance();

	@Override
	public void action() {
	
		TimePowerPrice data = new DbTimePowerPrice().getTimePowerPrice(null);
		
		while(data != null){
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					"GridAgent", "input", data);
			data = new DbTimePowerPrice().getTimePowerPrice(data.getDateTime());
			try {
				TimeUnit.SECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
