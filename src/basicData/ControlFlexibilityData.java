package basicData;

import java.util.Calendar;

@SuppressWarnings("serial")
public class ControlFlexibilityData extends FlexibilityData {

	private String idAgent;

	/**
	 * 
	 * @param idAgent
	 * @param type
	 * @param datetime
	 * @param lowerLimit
	 * @param upperLimit
	 * @param costKwh
	 * @param desideredChoice
	 */
	public ControlFlexibilityData(String idAgent, Calendar datetime, double lowerLimit, double upperLimit, 
			double costKwh, double desideredChoice, String type) 
	{
		super(datetime, lowerLimit, upperLimit, costKwh, desideredChoice, type);
		this.idAgent = idAgent;
	}
	
	public ControlFlexibilityData(String idAgent, FlexibilityData data)
	{
		super(data.getDatetime(), data.getLowerLimit(), data.getUpperLimit(), 
				data.getCostKwh(), data.getDesideredChoice(), data.getType());
		this.idAgent = idAgent;
	}
	
	public String getIdAgent() {
		return idAgent;
	}
	
	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}
	
}
