package basicData;

import java.util.Calendar;

public class DerData {

	private int idDer;
	private Calendar datetime;
	private double costKwh; 
	private double productionMin;
	private double productionMax;
	private double productionRequested;
	private double desideredChoice;
	
	/**
	 * 
	 * @param idDer
	 * @param datetime
	 * @param costKwh
	 * @param consumptionMin
	 * @param consumptionMax
	 * @param powerRequested
	 * @param desideredChoice
	 */
	public DerData(int idDer, Calendar datetime, double costKwh, double productionMin, double productionMax,
			double productionRequested, double desideredChoice) 
	{
		this.idDer = idDer;
		this.datetime = datetime;
		this.costKwh = costKwh;
		this.productionMin = productionMin;
		this.productionMax = productionMax;
		this.productionRequested = productionRequested;
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

	public double getProductionMin() {
		return productionMin;
	}

	public void setProductionMin(double productionMin) {
		this.productionMin = productionMin;
	}

	public double getProductionMax() {
		return productionMax;
	}

	public void setProductionMax(double productionMax) {
		this.productionMax = productionMax;
	}

	public double getProductionRequested() {
		return productionRequested;
	}

	public void setProductionRequested(double productionRequested) {
		this.productionRequested = productionRequested;
	}

	public double getDesideredChoice() {
		return desideredChoice;
	}

	public void setDesideredChoice(double desideredChoice) {
		this.desideredChoice = desideredChoice;
	}
}
