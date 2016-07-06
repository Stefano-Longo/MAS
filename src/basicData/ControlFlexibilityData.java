package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")
public class ControlFlexibilityData extends FlexibilityData {

	private String idAgent;
	private String type;
	
	public ControlFlexibilityData(String idAgent, String type, Calendar analysisDatetime, Calendar datetime, double lowerLimit, double upperLimit, double costKwh, double desideredChoice) {
		super(analysisDatetime, datetime, lowerLimit, upperLimit, costKwh, desideredChoice);
		this.idAgent = idAgent;
		this.type = type;
		
	}
	
	public String getIdAgent() {
		return idAgent;
	}
	
	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
