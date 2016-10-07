package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class ResultPowerPrice implements Serializable {

	private Calendar datetime;
	private double powerRequested; // positive if output from batteries, input otherwise
	private double costKwh; //in € per kw of energy in input or output
	
	public ResultPowerPrice(){}
	
	public ResultPowerPrice(Calendar datetime, double powerRequested, double costKwh) {
		this.datetime = (Calendar)datetime.clone();
		this.powerRequested = powerRequested;
		this.costKwh = costKwh;
	}
	
	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = (Calendar)datetime.clone();
	}

	public double residualPower() {
		return powerRequested;
	}
	
	public double getPowerRequested() {
		return powerRequested;
	}

	public void setPowerRequested(double powerRequested) {
		this.powerRequested = powerRequested;
	}
	
	public double getCostKwh() {
		return costKwh;
	}
	
	public void setCostKwh(double costKwh) {
		this.costKwh = costKwh;
	}
}
