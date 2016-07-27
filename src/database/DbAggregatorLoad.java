package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.AggregatorFlexibilityData;
import basicData.FlexibilityData;
import utils.GeneralData;

public class DbAggregatorLoad extends DbConnection {

	DateFormat format = new GeneralData().getFormat();
	
	public Boolean addFlexibilityLoadMessage (AggregatorFlexibilityData data)
	{
		String query = "INSERT INTO LoadAggregatorData (IdAggregatorAgent, IdLoad,"
				+ " DateTime, LowerLimit, UpperLimit, CostKwh, DesideredChoice)"
				+ " VALUES ('"+data.getIdAgent()+"',"+data.getIdentificator()+",'"
				+ format.format(data.getDatetime().getTime())+"',"+data.getLowerLimit()+","+data.getUpperLimit()+","
				+ data.getCostKwh()+","+data.getDesideredChoice()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public FlexibilityData aggregateMessageReceived (String idAggregatorAgent)
	{
		FlexibilityData data = null;
		String query = "SELECT DateTime, SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit,"
				+ " AVG(CostKwh) as CostKwh, SUM(DesideredChoice) as DesideredChoice"
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Datetime in (SELECT Max(Datetime)" 
									+"FROM LoadAggregatorData)"
				+ " GROUP BY DateTime";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getTimestamp("DateTime"));

				data = new FlexibilityData(cal2, rs.getDouble("LowerLimit"), 
						rs.getDouble("UpperLimit"), rs.getDouble("CostKwh"), 
						rs.getDouble("DesideredChoice"), "load");
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int countMessagesReceived (String idAgent)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM LoadAggregatorData"
    			+ " WHERE IdAggregatorAgent = '"+idAgent+"'"
    			+ " AND DateTime in (SELECT Max(DateTime)" 
									+"FROM LoadAggregatorData)";
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
	
	public ArrayList<AggregatorFlexibilityData> getLoadsChoice(String idAggregatorAgent)
	{
		ArrayList<AggregatorFlexibilityData> list = new ArrayList<AggregatorFlexibilityData>();
		String query = "SELECT *"
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
				+ " AND DateTime in (SELECT MAX(DateTime)"
								+" FROM LoadAggregatorData)"
				+ " ORDER BY CostKwh";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				AggregatorFlexibilityData data = new AggregatorFlexibilityData(
						rs.getString("IdAggregatorAgent"),rs.getInt("IdLoad"),cal,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"), "load");
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Boolean updateLastLoadConfirmedChoice(String idAggregatorAgent, int idLoad, boolean confirmed)
	{
		String query = "UPDATE LoadAggregatorData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND IdLoad = "+idLoad
				+ " AND DateTime IN (SELECT MAX(DateTime)"
									+ "	FROM ControlData)";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getLastConfirmedByChoice (String idAggregatorAgent, int idLoad, boolean confirmed)
	{
		String query = "SELECT COUNT(*) as Count"
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND IdLoad = "+idLoad
				+ " AND Confirmed = '"+confirmed+"'"
				+ " AND DateTime in (SELECT MAX(DateTime)"
								+" FROM LoadAggregatorData)";;
		try {
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
}
