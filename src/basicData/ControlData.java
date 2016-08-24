package basicData;

import java.util.Calendar;

public class ControlData {

	private String idAgent;
	private String idPlatform;
	private Calendar datetime;
	private double derPower;
	private double batteryPower;
	private double loadPower;
	private double gridPower;
	private double costKwh;
	private int confirmed;
	
	/**
	 * 
	 * @param idAgent
	 * @param idPlatform
	 * @param datetime
	 * @param derPower
	 * @param batteryPower
	 * @param loadPower
	 * @param gridPower
	 * @param costKwh
	 * @param confirmed
	 */
	public ControlData(String idAgent, String idPlatform, Calendar datetime, double derPower, double batteryPower,
			double loadPower, double gridPower, double costKwh, int confirmed) {
		this.idAgent = idAgent;
		this.idPlatform = idPlatform;
		this.datetime = datetime;
		this.derPower = derPower;
		this.batteryPower = batteryPower;
		this.loadPower = loadPower;
		this.gridPower = gridPower;
		this.costKwh = costKwh;
		this.confirmed = confirmed;
	}

	public String getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}

	public String getIdPlatform() {
		return idPlatform;
	}

	public void setIdPlatform(String idPlatform) {
		this.idPlatform = idPlatform;
	}

	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = datetime;
	}

	public double getDerPower() {
		return derPower;
	}

	public void setDerPower(double derPower) {
		this.derPower = derPower;
	}

	public double getBatteryPower() {
		return batteryPower;
	}

	public void setBatteryPower(double batteryPower) {
		this.batteryPower = batteryPower;
	}

	public double getLoadPower() {
		return loadPower;
	}

	public void setLoadPower(double loadPower) {
		this.loadPower = loadPower;
	}

	public double getGridPower() {
		return gridPower;
	}

	public void setGridPower(double gridPower) {
		this.gridPower = gridPower;
	}

	public double getCostKwh() {
		return costKwh;
	}

	public void setCostKwh(double costKwh) {
		this.costKwh = costKwh;
	}

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}
}
