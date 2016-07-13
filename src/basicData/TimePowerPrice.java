package basicData;

import java.io.Serializable;
import java.util.Calendar;


@SuppressWarnings("serial")
public class TimePowerPrice implements Serializable {
	
	/**
	 * This class will be a type of data
	 * It is the type of input message in the system
	 */
	private Calendar time = Calendar.getInstance();
	private double maxEnergy; //range in which I have that price, KW_min = 0
	private double energyPrice; // in € for each kw of change producing less or more
	
	public TimePowerPrice(Calendar time, double maxEnergy, double energyPrice) {
		this.time = time;
		this.maxEnergy = maxEnergy;
		this.energyPrice = energyPrice;
	}
	
	public TimePowerPrice(){	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(double maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public double getEnergyPrice() {
		return energyPrice;
	}

	public void setEnergyPrice(double energyPrice) {
		this.energyPrice = energyPrice;
	}


	
}
