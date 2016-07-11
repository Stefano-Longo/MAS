package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;

import basicData.AggregatorFlexibilityData;
import basicData.FlexibilityData;

public class DbAggregatorBattery extends DbConnection {

	/**
	 * 
	 * @param idAgent
	 * @param data
	 * @return
	 */
	public Boolean addFlexibilityBatteryMessage (AggregatorFlexibilityData data)
	{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String query = "INSERT INTO BatteryAggregatorData (IdAggregatorAgent, IdBattery, AnalysisDateTime,"
				+ " DateTime, LowerLimit, UpperLimit, CostKwh, DesideredChoice)"
				+ " VALUES ('"+data.getIdAgent()+"',"+data.getIdentificator()+",'"+format.format(data.getAnalysisDatetime().getTime())+"','"
				+ format.format(data.getDatetime().getTime())+"',"+data.getLowerLimit()+","+data.getUpperLimit()+","
				+ data.getCostKwh()+","+data.getDesideredChoice()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @param idAgent
	 * @param dateTime
	 * @return
	 */
	public ArrayList<FlexibilityData> aggregateMessageReceived (String idAggregatorAgent)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String query = "SELECT AnalysisDateTime, Datetime, SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit,"
				+ " AVG(CostKwh) as CostKwh, SUM(DesideredChoice) as DesideredChoice"
				+ " FROM BatteryAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND AnalysisDateTime in (SELECT Max(AnalysisDateTime)" 
											+"FROM BatteryAggregatorData)"
				+ " GROUP BY DateTime";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("AnalysisDateTime"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("DateTime"));
//TO-DO!!!
				FlexibilityData data = new FlexibilityData(cal1, cal2, rs.getDouble("LowerLimit"), 
						rs.getDouble("UpperLimit"), rs.getDouble("CostKwh"), 
						rs.getDouble("DesideredChoice"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @param idAgent
	 * @param dateTime
	 * @return 
	 */
	public int countMessagesReceived (String idAgent)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM BatteryAggregatorData"
    			+ " WHERE IdAggregatorAgent = '"+idAgent+"'"
    			+ " AND AnalysisDateTime in (SELECT Max(AnalysisDateTime)" 
											+"FROM BatteryAggregatorData)";
		try{
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				return rs.getInt("Count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * @param myAgent
	 * @return
	 */
	public ArrayList<AggregatorFlexibilityData> getBatteriesChoice(String idAggregatorAgent)
	{
		ArrayList<AggregatorFlexibilityData> list = new ArrayList<AggregatorFlexibilityData>();
		String query = "SELECT IdAggregatorAgent, IdBattery, LowerLimit, UpperLimit, CostKwh,"
				+ " DesideredChoice, DesideredChoice-LowerLimit as Diff"
				+ " FROM BatteryAggregatorData A JOIN Battery B ON A.IdBattery = B.IdBattery"
				+ " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
				+ " AND AnalysisDateTime in (SELECT MAX(AnalysisDateTime)"
											+" FROM BatteryAggregatorData"
				+ " ORDER BY Diff";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("AnalysisDateTime"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("DateTime"));
				
				AggregatorFlexibilityData data = new AggregatorFlexibilityData(
						rs.getString("IdAggregatorAgent"),rs.getInt("IdBattery"),cal1, cal2,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ArrayList<AggregatorFlexibilityData> getBatteriesChoiceByValue(String idAggregatorAgent, String choice)
	{
		ArrayList<AggregatorFlexibilityData> list = new ArrayList<AggregatorFlexibilityData>();
		String query = "SELECT IdAggregatorAgent, IdBattery, LowerLimit, UpperLimit, CostKwh,"
				+ " DesideredChoice, DesideredChoice-LowerLimit as Diff"
				+ " FROM BatteryAggregatorData A JOIN Battery B ON A.IdBattery = B.IdBattery";
		if(choice.equals("positive"))
		{
			query += " WHERE DesideredChoice > 0";
		}
		else if(choice.equals("negative"))
		{
			query += " WHERE DesideredChoice < 0";
		}
		query += " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
				+ " AND AnalysisDateTime in (SELECT MAX(AnalysisDateTime)"
											+" FROM BatteryAggregatorData"
				+ " ORDER BY Diff";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("AnalysisDateTime"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("DateTime"));
				
				AggregatorFlexibilityData data = new AggregatorFlexibilityData(
						rs.getString("IdAggregatorAgent"),rs.getInt("IdBattery"),cal1, cal2,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
}
