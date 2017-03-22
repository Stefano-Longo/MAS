package basicData;

import java.util.Calendar;

public class BatteryData {

	private int idBattery;
	private Calendar datetime;
	private double socObjective;
	private double soc;
	private double costKwh;
	private double inputPowerMax;
	private double outputPowerMax;
	private double powerRequested;
	private double desiredChoice;
	
	//not for upload in db
	private Calendar analisysDatetime;
	private double capacity;
	private String type;

	/**
	 * 
	 * @param idBattery
	 * @param datetime
	 * @param socObjective
	 * @param soc
	 * @param costKwh
	 * @param inputPowerMax
	 * @param outputPowerMax
	 * @param powerRequested
	 * @param desiredChoice
	 */
	public BatteryData(int idBattery, Calendar datetime, double socObjective, double soc, double costKwh, 
			double inputPowerMax, double outputPowerMax, double powerRequested, double desiredChoice)
	{
		this.idBattery = idBattery;
		this.datetime = (Calendar)datetime.clone();
		this.socObjective = socObjective;
		this.soc = soc;
		this.costKwh = costKwh;
		this.inputPowerMax = inputPowerMax;
		this.outputPowerMax = outputPowerMax;
		this.powerRequested = powerRequested;
		this.desiredChoice = desiredChoice;
	}
	
	/**
	 * 
	 * @param idBattery
	 * @param datetime
	 * @param socObjective
	 * @param soc
	 * @param costKwh
	 * @param powerRequested
	 */
	public BatteryData(int idBattery, Calendar datetime, double socObjective, double soc, double costKwh,
			double powerRequested)
	{
		this.idBattery = idBattery;
		this.datetime = (Calendar)datetime.clone();
		this.socObjective = socObjective;
		this.soc = soc;
		this.costKwh = costKwh;
		this.powerRequested = powerRequested;
	}
	
	public BatteryData() { }
	
	public double getCapacity() {
		return capacity;
	}

	public Calendar getAnalisysDatetime() {
		return analisysDatetime;
	}

	public void setAnalisysDatetime(Calendar analisysDatetime) {
		this.analisysDatetime = (Calendar)analisysDatetime.clone();
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getIdBattery() {
		return idBattery;
	}

	public void setIdBattery(int idBattery) {
		this.idBattery = idBattery;
	}

	public double getInputPowerMax() {
		return inputPowerMax;
	}

	public void setInputPowerMax(double inputPowerMax) {
		this.inputPowerMax = inputPowerMax;
	}

	public double getOutputPowerMax() {
		return outputPowerMax;
	}

	public void setOutputPowerMax(double outputPowerMax) {
		this.outputPowerMax = outputPowerMax;
	}

	public double getPowerRequested() {
		return powerRequested;
	}

	public void setPowerRequested(double powerRequested) {
		this.powerRequested = powerRequested;
	}

	public void setSoc(double soc) {
		this.soc = soc;
	}

	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = (Calendar)datetime.clone();
	}

	public double getSocObjective() {
		return socObjective;
	}

	public void setSocObjective(double socObjective) {
		this.socObjective = socObjective;
	}

	public double getSoc() {
		return soc;
	}

	public double getCostKwh() {
		return costKwh;
	}

	public void setCostKwh(double costKwh) {
		this.costKwh = costKwh;
	}

	public double getDesiredChoice() {
		return desiredChoice;
	}

	public void setDesiredChoice(double desiredChoice) {
		this.desiredChoice = desiredChoice;
	}
	
}
