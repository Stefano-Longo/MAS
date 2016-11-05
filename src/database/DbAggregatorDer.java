package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.FlexibilityData;
import utils.GeneralData;


public class DbAggregatorDer extends DbConnection {

	DateFormat format = GeneralData.getFormat();
	
	public Boolean addFlexibilityDerMessage (String idAggregatorAgent, FlexibilityData data)
	{
		String query = "INSERT INTO DerAggregatorData (IdAggregatorAgent, IdDer,"
				+ " DateTime, LowerLimit, UpperLimit, CostKwh, DesideredChoice)"
				+ " VALUES ('"+idAggregatorAgent+"',"+data.getIdAgent()+",'"
				+ format.format(data.getDatetime().getTime())+"',"+data.getLowerLimit()+","+data.getUpperLimit()+","
				+ data.getCostKwh()+","+data.getDesideredChoice()+")";
		//System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return false;
	}
	
	public FlexibilityData aggregateMessagesReceived (String idAggregatorAgent, Calendar datetime)
	{
		FlexibilityData data = new FlexibilityData();
		String query = "SELECT IdAggregatorAgent, DateTime, SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit,"
				+ " AVG(CostKwh) as CostKwh, SUM(DesideredChoice) as DesideredChoice"
				+ " FROM DerAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Datetime = '"+format.format(datetime.getTime())+"'"
				+ " GROUP BY DateTime, IdAggregatorAgent";
		//System.out.println(query);
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				data = new FlexibilityData(rs.getString("IdAggregatorAgent"), cal, rs.getDouble("LowerLimit"), 
						rs.getDouble("UpperLimit"), GeneralData.round(rs.getDouble("CostKwh"), 5), 
						rs.getDouble("DesideredChoice"), "der");
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	public int countMessagesReceived (String idAgent, Calendar datetime)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM DerAggregatorData"
    			+ " WHERE IdAggregatorAgent = '"+idAgent+"'"
    			+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try{
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				return rs.getInt("Count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return 0;
	}
	
	public ArrayList<FlexibilityData> getDersChoice(String idAggregatorAgent, Calendar datetime)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		String query = "SELECT *"
				+ " FROM DerAggregatorData"
				+ " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
				+ " ORDER BY CostKwh";
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				FlexibilityData data = new FlexibilityData(rs.getString("IdDer"),cal,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"), "der");
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return list;
	}
	
	public Boolean updateLastDerConfirmedChoice(String idAggregatorAgent, int idDer, int confirmed, Calendar datetime)
	{
		String query = "UPDATE DerAggregatorData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND IdDer = "+idDer
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		//System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return false;
	}
	
	public int getLastConfirmedByChoice (String idAggregatorAgent, int confirmed, Calendar datetime)
	{
		String query = "SELECT COUNT(*) as Count"
				+ " FROM DerAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Confirmed = '"+confirmed+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		//System.out.println(query);
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				return rs.getInt("Count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return 0;
	}
}
