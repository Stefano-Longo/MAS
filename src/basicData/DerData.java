package basicData;

import java.util.Calendar;

public class DerData {

	private int idDer;
	private Calendar datetime = Calendar.getInstance();;
	private double costKwh; 
	private double powerRequested;
	private double desideredChoice;
	
	public DerData(int idDer, Calendar datetime, double costKwh, double powerRequested, double desideredChoice) 
	{
		this.idDer = idDer;
		this.datetime = datetime;
		this.costKwh = costKwh;
		this.powerRequested = powerRequested;
		this.desideredChoice = desideredChoice;
	}

	public int getIdDer() {
		return idDer;
	}

	public void setIdDer(int idDer) {
		this.idDer = idDer;
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
	
}
