package basicData;

import java.util.Calendar;

public class PeakData {

	private String idAggregatorAgent;
	private Calendar datetime;
	private double peakValue;
	
	public PeakData(String idAggregatorAgent, Calendar datetime, double peakValue) {
		this.idAggregatorAgent = idAggregatorAgent;
		this.datetime = datetime;
		this.peakValue = peakValue;
	}
	
	public String getIdAggregatorAgent() {
		return idAggregatorAgent;
	}
	public void setIdAggregatorAgent(String idAggregatorAgent) {
		this.idAggregatorAgent = idAggregatorAgent;
	}
	public Calendar getDatetime() {
		return datetime;
	}
	public void setDatetime(Calendar datetime) {
		this.datetime = datetime;
	}
	public double getPeakValue() {
		return peakValue;
	}
	public void setPeakValue(double peakValue) {
		this.peakValue = peakValue;
	}

	
}
