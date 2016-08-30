package behaviours;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import agents.BaseAgent;
import basicData.TimePowerPrice;
import database.DbTimePowerPrice;
import jade.core.behaviours.Behaviour;
import utils.GeneralData;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class TsoBehaviour extends Behaviour {

	Calendar datetime = Calendar.getInstance();

	@Override
	public void action() {
		DateFormat format = GeneralData.getFormat();
		try {
			datetime.setTime(format.parse("2016-06-30 23:00"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		TimePowerPrice data = new DbTimePowerPrice().getTimePowerPrice(datetime);
		
		while(data != null){
			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
					"GridAgent", "input", data);
			data = new DbTimePowerPrice().getTimePowerPrice(data.getDateTime());
			try {
				TimeUnit.SECONDS.sleep(25);
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
