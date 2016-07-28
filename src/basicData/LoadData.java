package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class LoadData implements Serializable {
	
	private int idLoad;
	private Calendar datetime;
	private double costKwh;
	private double criticalConsumption; 
	private double nonCriticalConsumption; 
	private double consumptionMin;
	private double consumptionMax;
	private double powerRequested;
	private double desideredChoice;
	private double consumptionShifted;
	private Calendar toDatetime;

	/**
	 * 
	 * @param idLoad
	 * @param datetime
	 * @param costKwh
	 * @param criticalConsumption
	 * @param nonCriticalConsumption
	 * @param consumptionMin
	 * @param consumptionMax
	 * @param powerRequested
	 * @param desideredChoice
	 * @param consumptionShifted
	 * @param toDatetime
	 */
	public LoadData(int idLoad, Calendar datetime, double costKwh, double criticalConsumption, 
			double nonCriticalConsumption, double consumptionMin, double consumptionMax,
			double powerRequested, double desideredChoice, double consumptionShifted, Calendar toDatetime) 
	{
		this.idLoad = idLoad;
		this.datetime = datetime;
		this.costKwh = costKwh;
		this.criticalConsumption = criticalConsumption;
		this.nonCriticalConsumption = nonCriticalConsumption;
		this.consumptionMax = consumptionMax;
		this.consumptionMin = consumptionMin;
		this.powerRequested = powerRequested;
		this.desideredChoice = desideredChoice;
		this.consumptionShifted = consumptionShifted;
		this.toDatetime = toDatetime;
	}
	
	public LoadData(int idLoad, Calendar datetime, double powerRequested, double consumptionShifted) 
	{
		this.idLoad = idLoad;
		this.datetime = datetime;
		this.powerRequested = powerRequested;
		this.consumptionShifted = consumptionShifted;
	}
	
	public LoadData() { }

	public int getIdLoad() {
		return idLoad;
	}

	public void setIdLoad(int idLoad) {
		this.idLoad = idLoad;
	}

	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = datetime;
	}

	public double getCostKwh() {
		return costKwh;
	}

	public void setCostKwh(double costKwh) {
		this.costKwh = costKwh;
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

	public double getConsumptionMin() {
		return consumptionMin;
	}

	public void setConsumptionMin(double consumptionMin) {
		this.consumptionMin = consumptionMin;
	}

	public double getConsumptionMax() {
		return consumptionMax;
	}

	public void setConsumptionMax(double consumptionMax) {
		this.consumptionMax = consumptionMax;
	}

	public double getPowerRequested() {
		return powerRequested;
	}

	public void setPowerRequested(double powerRequested) {
		this.powerRequested = powerRequested;
	}

	public double getDesideredChoice() {
		return desideredChoice;
	}

	public void setDesideredChoice(double desideredChoice) {
		this.desideredChoice = desideredChoice;
	}
	
	public double getConsumptionShifted() {
		return consumptionShifted;
	}

	public void setConsumptionShifted(double consumptionShifted) {
		this.consumptionShifted = consumptionShifted;
	}

	public Calendar getToDatetime() {
		return toDatetime;
	}

	public void setToDatetime(Calendar toDatetime) {
		this.toDatetime = toDatetime;
	}
}
