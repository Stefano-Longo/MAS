package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.FlexibilityData;
import basicData.TimePowerPrice;
import utils.GeneralData;

public class DbAggregatorLoad extends DbConnection {

	static DateFormat format = GeneralData.getFormat();

	public boolean deleteOldForecastDataByIdLoad (String idAggregatorAgent, String idAgent, Statement stmt)
	{
		String query = "DELETE FROM LoadAggregatorData "
				+ " WHERE RTRIM(IdAggregatorAgent) = '"+idAggregatorAgent+"'"
				+ " AND IdLoad = '"+idAgent+"'"
				+ " AND Forecast = 1";
		//System.out.println(query);
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deleteOldNonForecastDataByIdLoad (String idAggregatorAgent, String idAgent, Calendar datetime, Statement stmt)
	{
		String query = "DELETE FROM LoadAggregatorData "
				+ " WHERE RTRIM(IdAggregatorAgent) = '"+idAggregatorAgent+"'"
				+ " AND IdLoad = '"+idAgent+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		//System.out.println(query);
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean addFlexibilityLoadMessage (String idAggregatorAgent, ArrayList<FlexibilityData> data, Statement stmt)
	{
		for(int i=0; i<data.size(); i++)
		{
			int forecast = i==0 ? 0 : 1;
			String query = "INSERT INTO LoadAggregatorData (IdAggregatorAgent, IdLoad,"
					+ " DateTime, LowerLimit, UpperLimit, CostKwh, DesideredChoice, Forecast)"
					+ " VALUES ('"+idAggregatorAgent+"',"+data.get(i).getIdAgent()+",'"
					+ format.format(data.get(i).getDatetime().getTime())+"',"+data.get(i).getLowerLimit()+","+data.get(i).getUpperLimit()+","
					+ data.get(i).getCostKwh()+","+data.get(i).getDesideredChoice()+", "+forecast+")";
			//System.out.println(query);
			try {
				stmt.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connClose();
		return true;
	}
	
	public ArrayList<TimePowerPrice> checkThreshold(String idAggregatorAgent, Calendar datetime, Statement stmt)
	{
		ArrayList<TimePowerPrice> list = new ArrayList<TimePowerPrice>();
		String query = "SELECT p.DateTime, MinimumConsumption, Threshold"
					+ " FROM Price p JOIN (SELECT DateTime, SUM(LowerLimit) as MinimumConsumption"
								+ " FROM LoadAggregatorData  "
								+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
									+" AND Forecast = 1"
								+ " GROUP BY DateTime) loads "
							+ "ON p.DateTime = loads.DateTime ";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				TimePowerPrice data = new TimePowerPrice(cal, GeneralData.round(rs.getDouble("Threshold"), 2), 
						GeneralData.round(rs.getDouble("MinimumConsumption"), 2));
				list.add(data);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	
	public FlexibilityData aggregateMessagesReceived (String idAggregatorAgent, Calendar datetime)
	{
		FlexibilityData data = new FlexibilityData();
		String query = "SELECT IdAggregatorAgent, DateTime, SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit,"
				+ " AVG(CostKwh) as CostKwh, SUM(DesideredChoice) as DesideredChoice"
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Datetime = '"+format.format(datetime.getTime())+"'"
				+ " GROUP BY DateTime, IdAggregatorAgent";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				data = new FlexibilityData(rs.getString("IdAggregatorAgent"),cal, GeneralData.round(rs.getDouble("LowerLimit"),2), 
						GeneralData.round(rs.getDouble("UpperLimit"),2), GeneralData.round(rs.getDouble("CostKwh"), 5), 
						GeneralData.round(rs.getDouble("DesideredChoice"),2), "load");
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
    			+ " FROM LoadAggregatorData"
    			+ " WHERE IdAggregatorAgent = '"+idAgent+"'"
	    			+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
					+ " AND Forecast = 0";
		System.out.println(query);
		try{
			ResultSet rs = stmt.executeQuery(query);
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
	
	public ArrayList<FlexibilityData> getLoadsChoice(String idAggregatorAgent, Calendar datetime)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		String query = "SELECT *"
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
					+ " AND Forecast = 0"
				+ " ORDER BY CostKwh";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				FlexibilityData data = new FlexibilityData(rs.getString("IdLoad"),cal,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"), "load");
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return list;
	}
	
	public Boolean updateLastLoadConfirmedChoice(String idAggregatorAgent, int idLoad, int confirmed, Calendar datetime)
	{
		String query = "UPDATE LoadAggregatorData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
					+ " AND IdLoad = "+idLoad
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
				+ " FROM LoadAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Confirmed = '"+confirmed+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
				+ " AND Forecast = 0";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
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
