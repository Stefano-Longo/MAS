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
	private double desiredChoice;
	private double consumptionShifted;
	private Calendar toDatetime;
	private int solutionNumber;

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
	 * @param desiredChoice
	 * @param consumptionShifted
	 * @param toDatetime
	 */
	public LoadData(int idLoad, Calendar datetime, double costKwh, double criticalConsumption, 
			double nonCriticalConsumption, double consumptionMin, double consumptionMax, double powerRequested,
			double desiredChoice, double consumptionShifted, Calendar toDatetime, int solutionNumber) 
	{
		this.idLoad = idLoad;
		this.datetime = (Calendar)datetime.clone();
		this.costKwh = costKwh;
		this.criticalConsumption = criticalConsumption;
		this.nonCriticalConsumption = nonCriticalConsumption;
		this.consumptionMax = consumptionMax;
		this.consumptionMin = consumptionMin;
		this.powerRequested = powerRequested;
		this.desiredChoice = desiredChoice;
		this.consumptionShifted = consumptionShifted;
		this.toDatetime = toDatetime == null ? null : (Calendar)toDatetime.clone();
		this.solutionNumber = solutionNumber;
	}
	
	public LoadData(int idLoad, Calendar datetime, double powerRequested, double consumptionShifted) 
	{
		this.idLoad = idLoad;
		this.datetime = (Calendar)datetime.clone();
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
		this.datetime = (Calendar)datetime.clone();
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

	public double getDesiredChoice() {
		return desiredChoice;
	}

	public void setDesiredChoice(double desiredChoice) {
		this.desiredChoice = desiredChoice;
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
		this.toDatetime = (Calendar)toDatetime.clone();
	}

	public int getSolutionNumber() {
		return solutionNumber;
	}

	public void setSolutionNumber(int solutionNumber) {
		this.solutionNumber = solutionNumber;
	}
}
