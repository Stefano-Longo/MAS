package database;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.ArrayList;

import basicData.FlexibilityData;
import utils.GeneralData;

public class DbAggregatorBattery extends DbConnection {

	/**
	 * 
	 * @param idAgent
	 * @param data
	 * @return
	 */
	DateFormat format = GeneralData.getFormat();

	public Boolean addFlexibilityBatteryMessage (String idAggregatorAgent, FlexibilityData data)
	{
		String query = "INSERT INTO BatteryAggregatorData (IdAggregatorAgent, IdBattery,"
				+ " DateTime, InputPowerMax, OutputPowerMax, CostKwh, DesiredChoice)"
				+ " VALUES ('"+idAggregatorAgent+"',"+data.getIdAgent()+",'"
				+ format.format(data.getDatetime().getTime())+"',"+data.getLowerLimit()+","+data.getUpperLimit()+","
				+ data.getCostKwh()+","+data.getDesiredChoice()+")";
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
	
	
	
	/**
	 * 
	 * @param idAgent
	 * @param dateTime
	 * @return
	 */
	public FlexibilityData aggregateMessagesReceived (String idAggregatorAgent, Calendar datetime)
	{
		FlexibilityData data = new FlexibilityData();
		String query = "SELECT DateTime, SUM(InputPowerMax) as InputPowerMax, SUM(OutputPowerMax) as OutputPowerMax,"
				+ " AVG(CostKwh) as CostKwh, SUM(DesiredChoice) as DesiredChoice"
				+ " FROM BatteryAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Datetime = '"+format.format(datetime.getTime())+"'"
				+ " GROUP BY DateTime";
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{	
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getTimestamp("DateTime"));
				
				data = new FlexibilityData(idAggregatorAgent, cal2, rs.getDouble("InputPowerMax"), 
						rs.getDouble("OutputPowerMax"), GeneralData.round(rs.getDouble("CostKwh"), 5), 
						rs.getDouble("DesiredChoice"), "battery");
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	/**
	 * 
	 * @param idAgent
	 * @param dateTime
	 * @return 
	 */
	public int countMessagesReceived (String idAgent, Calendar datetime)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM BatteryAggregatorData"
    			+ " WHERE IdAggregatorAgent = '"+idAgent+"'"
    			+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		//System.out.println(query);
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
	
	/**
	 * 
	 * @param myAgent
	 * @return
	 */
	public ArrayList<FlexibilityData> getBatteriesChoice(String idAggregatorAgent, Calendar datetime)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		String query = "SELECT *, InputPowerMax-DesiredChoice as Diff"
				+ " FROM BatteryAggregatorData"
				+ " WHERE IdAggregatorAgent='"+idAggregatorAgent+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
				+ " ORDER BY Diff, IdBattery";
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				FlexibilityData data = new FlexibilityData(rs.getString("IdBattery"),cal,
						rs.getDouble("InputPowerMax"), rs.getDouble("OutputPowerMax"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesiredChoice"), "battery");
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		
		return list;
	}
	
	public ArrayList<FlexibilityData> getBatteriesChoiceByValue(String idAggregatorAgent, 
			String choice, Calendar datetime)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		String query = "SELECT *, InputPowerMax-DesiredChoice as Diff"
				+ " FROM BatteryAggregatorData A JOIN Battery B ON A.IdBattery = B.IdBattery"
				+ " WHERE";
		if(choice.equals("positive"))
		{
			query += " DesiredChoice > 0 AND";
		}
		else if(choice.equals("negative"))
		{
			query += " DesiredChoice < 0 AND";
		}
		query += " IdAggregatorAgent='"+idAggregatorAgent+"'"
			   + " AND DateTime = '"+format.format(datetime.getTime())+"'"
			   + " ORDER BY Diff, B.IdBattery";
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				FlexibilityData data = new FlexibilityData(
						rs.getString("IdBattery"),cal, rs.getDouble("InputPowerMax"),
						rs.getDouble("OutputPowerMax"), rs.getDouble("CostKwh"), 
						rs.getDouble("DesiredChoice"), "battery");
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		
		return list;
	}
	
	public Boolean updateLastBatteryConfirmedChoice(String idAggregatorAgent, int idBattery, int confirmed, Calendar datetime)
	{
		String query = "UPDATE BatteryAggregatorData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND IdBattery = "+idBattery
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
				+ " FROM BatteryAggregatorData"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND Confirmed = '"+confirmed+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
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
