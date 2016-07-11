package basicData;

import java.util.Calendar;

import jade.core.AID;

/**
 * TO-DO tabella db controldata
 * @author Longo
 *
 */
public class ControlData {

	private AID idAgent;
	private String idPlatform;
	private Calendar datetime;
	private double derPower;
	private double batteryPower;
	private double loadPower;
	
	public ControlData(AID idAgent, String idPlatform, Calendar datetime, double derPower,
			double batteryPower, double loadPower) {
		this.idAgent = idAgent;
		this.idPlatform = idPlatform;
		this.datetime = datetime;
		this.derPower = derPower;
		this.batteryPower = batteryPower;
		this.loadPower = loadPower;
	}

	public AID getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(AID idAgent) {
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
}
