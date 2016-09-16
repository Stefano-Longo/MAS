package agents;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import basicData.TimePowerPrice;
import behaviours.ReceiveMessages;
import behaviours.TsoBehaviour;
import database.DbTimePowerPrice;
import utils.GeneralData;

@SuppressWarnings("serial")
public class TsoAgent extends BaseAgent {
	
	protected void setup(){
		registerDfAgent(this.getHap(), "TsoAgent");
		
		Calendar datetime = Calendar.getInstance();
		DateFormat format = GeneralData.getFormat();
		
		try {
			datetime.setTime(format.parse("2016-07-27 23:00"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		TimePowerPrice data = new DbTimePowerPrice().getTimePowerPriceByDateTime(datetime);
		this.addBehaviour(new TsoBehaviour(data));
		this.addBehaviour(new ReceiveMessages(this));
	}
}
