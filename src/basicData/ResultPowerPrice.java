package basicData;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ResultPowerPrice implements Serializable {

	private double powerRequested; // positive if output from batteries, input otherwise
	private double costKwh; //in € per kw of energy in input or output
	
	public ResultPowerPrice(double powerRequested, double costKwh) {
		this.powerRequested = powerRequested;
		this.costKwh = costKwh;
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
