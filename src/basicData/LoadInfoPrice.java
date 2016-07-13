package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")
public class LoadInfoPrice extends LoadInfo {

	private Calendar toDatetime;
	private double price;
	
	public LoadInfoPrice(int idLoad, String idAgent, String platform, Calendar datetime, double criticalConsumption,
			double nonCriticalConsumption, double consumptionAdded) {
		super(idLoad, idAgent, platform, datetime, criticalConsumption, nonCriticalConsumption, consumptionAdded);
	}

	public Calendar getToDatetime() {
		return toDatetime;
	}

	public void setToDatetime(Calendar toDatetime) {
		this.toDatetime = toDatetime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
