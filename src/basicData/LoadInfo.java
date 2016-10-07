package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class LoadInfo implements Serializable {

	private int idLoad;
	private String idAgent;
	private String idPlatform;
	private Calendar datetime;
	private double criticalConsumption;
	private double nonCriticalConsumption;
	private double consumptionAdded;
	
	public LoadInfo(int idLoad, String idAgent, String idPlatform, Calendar datetime, double criticalConsumption,
			double nonCriticalConsumption, double consumptionAdded) {
		this.idLoad = idLoad;
		this.idAgent = idAgent;
		this.idPlatform = idPlatform;
		this.datetime = (Calendar)datetime.clone();
		this.criticalConsumption = criticalConsumption;
		this.nonCriticalConsumption = nonCriticalConsumption;
		this.consumptionAdded = consumptionAdded;
	}

	public LoadInfo(int idLoad, Calendar datetime, double consumptionAdded)
	{
		this.idLoad = idLoad;
		this.datetime = (Calendar)datetime.clone();
		this.consumptionAdded = consumptionAdded;
	}
	
	public LoadInfo() { }
	
	public int getIdLoad() {
		return idLoad;
	}

	public void setIdLoad(int idLoad) {
		this.idLoad = idLoad;
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
		this.datetime = (Calendar)datetime.clone();
	}

	public double getCriticalConsumption() {
		return criticalConsumption;
	}

	public void setCriticalConsumption(double criticalConsumption) {
		this.criticalConsumption = criticalConsumption;
	}

	public double getNonCriticalConsumption() {
		return nonCriticalConsumption;
	}

	public void setNonCriticalConsumption(double nonCriticalConsumption) {
		this.nonCriticalConsumption = nonCriticalConsumption;
	}

	public double getConsumptionAdded() {
		return consumptionAdded;
	}

	public void setConsumptionAdded(double consumptionAdded) {
		this.consumptionAdded = consumptionAdded;
	}
}
