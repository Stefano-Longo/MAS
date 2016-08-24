package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class TimePowerPrice implements Serializable {
	
	/**
	 * This class will be a type of data
	 * It is the type of input message in the system
	 */
	private Calendar dateTime;
	private double threshold; //range in which I have that price, KW_min = 0
	private double energyPrice; // in € for each kw of change producing less or more
	
	/**
	 * 
	 * @param dateTime
	 * @param maxEnergy
	 * @param energyPrice
	 */
	public TimePowerPrice(Calendar dateTime, double threshold, double energyPrice) 
	{
		this.dateTime = dateTime;
		this.threshold = threshold;
		this.energyPrice = energyPrice;
	}
	
	public TimePowerPrice(){	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getEnergyPrice() {
		return energyPrice;
	}

	public void setEnergyPrice(double energyPrice) {
		this.energyPrice = energyPrice;
	}


	
}
