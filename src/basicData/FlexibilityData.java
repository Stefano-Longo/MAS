package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class FlexibilityData implements Serializable {

	private String idAgent;
	private Calendar datetime;
	private double lowerLimit;
	private double upperLimit;
	private double costKwh; 
	private double desideredChoice; // positive or negative value which represent the choice the single agent
	private String type;


	/**
	 * 
	 * @param idAgent
	 * @param datetime
	 * @param lowerLimit
	 * @param upperLimit
	 * @param costKwh
	 * @param desideredChoice
	 * @param type
	 */
	public FlexibilityData(String idAgent, Calendar datetime, double lowerLimit, double upperLimit, 
			double costKwh, double desideredChoice, String type) {
		this.idAgent = idAgent;
		this.datetime = (Calendar)datetime.clone();
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.costKwh = costKwh;
		this.desideredChoice = desideredChoice;
		this.type = type;
	}

	public FlexibilityData(String idAgent, Calendar datetime, double lowerLimit, double upperLimit, 
			double costKwh, double desideredChoice) {
		this.idAgent = idAgent;
		this.datetime = (Calendar)datetime.clone();
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.costKwh = costKwh;
		this.desideredChoice = desideredChoice;
	}
	
	public FlexibilityData() {}
	
	public String getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}

	public double getCostKwh() {
		return costKwh;
	}

	public void setCostKwh(double costKwh) {
		this.costKwh = costKwh;
	}

	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = (Calendar)datetime.clone();
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getDesideredChoice() {
		return desideredChoice;
	}

	public void setDesideredChoice(double desideredChoice) {
		this.desideredChoice = desideredChoice;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
