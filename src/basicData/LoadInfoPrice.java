package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")
public class LoadInfoPrice extends LoadInfo {

	private double price;
	private Calendar toDatetime;

	public LoadInfoPrice(int idLoad, String idAgent, String platform, Calendar datetime, double criticalConsumption,
			double nonCriticalConsumption, double consumptionAdded) {
		super(idLoad, idAgent, platform, datetime, criticalConsumption, nonCriticalConsumption, consumptionAdded);
	}
	
	public LoadInfoPrice(int idLoad, String idAgent, String platform, Calendar datetime, double criticalConsumption,
			double nonCriticalConsumption, double consumptionAdded, double price, Calendar toDatetime) {
		super(idLoad, idAgent, platform, datetime, criticalConsumption, nonCriticalConsumption, consumptionAdded);
		this.price = price;
		this.toDatetime = (Calendar)toDatetime.clone();
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public Calendar getToDatetime() {
		return toDatetime;
	}

	public void setToDatetime(Calendar toDatetime) {
		this.toDatetime = (Calendar)toDatetime.clone();
	}
}
