package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")
public class AggregatorFlexibilityData extends FlexibilityData {
	
	private String idAgent;
	private int identificator;

	public AggregatorFlexibilityData(String idAggregatorAgent, int identificator, Calendar analysisDatetime, Calendar datetime, double lowerLimit, 
			double upperLimit, double costKwh, double desideredChoice) 
	{
		super(analysisDatetime, datetime, lowerLimit, upperLimit, costKwh, desideredChoice);
		this.idAgent = idAggregatorAgent;
		this.identificator = identificator;
	}

	public String getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}

	public int getIdentificator() {
		return identificator;
	}
	
	public void setIdentificator(int identificator) {
		this.identificator = identificator;
	}
}
