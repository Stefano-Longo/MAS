package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")

public class AggregatorFlexibilityData extends FlexibilityData {
	
	private String idAgent;
	private int identificator;

	/**
	 * 
	 * @param idAggregatorAgent
	 * @param identificator
	 * @param datetime
	 * @param lowerLimit
	 * @param upperLimit
	 * @param costKwh
	 * @param desideredChoice
	 */
	public AggregatorFlexibilityData(String idAggregatorAgent, int identificator, Calendar datetime, double lowerLimit, 
			double upperLimit, double costKwh, double desideredChoice, String type) 
	{
		super(datetime, lowerLimit, upperLimit, costKwh, desideredChoice, type);
		this.idAgent = idAggregatorAgent;
		this.identificator = identificator;
	}

	public AggregatorFlexibilityData(String idAggregatorAgent, int identificator, FlexibilityData data)
	{
		super(data.getDatetime(), data.getLowerLimit(), data.getUpperLimit(),
				data.getCostKwh(), data.getDesideredChoice(), data.getType());
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
