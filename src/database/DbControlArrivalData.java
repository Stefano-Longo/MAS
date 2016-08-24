package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.ControlFlexibilityData;
import utils.GeneralData;

public class DbControlArrivalData extends DbConnection {

	DateFormat format = GeneralData.getFormat();

	public Boolean addControlArrivalData (ControlFlexibilityData data)
	{
		String query = "INSERT INTO ControlArrivalData (IdControlAgent, DateTime,"
				+ " LowerLimit, UpperLimit, CostKwh, DesideredChoice, Type)"
				+ " VALUES ('"+data.getIdAgent()+"','"+format.format(data.getDatetime().getTime())+"',"
				+data.getLowerLimit()+","+ data.getUpperLimit()+","+data.getCostKwh()+","
				+data.getDesideredChoice()+",'"+data.getType()+"')";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ControlFlexibilityData getLastControlArrivalData (String idControlAgent, String type)
	{
		String query = "SELECT SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit, AVG(CostKwh) as CostKwh,"
				+ " SUM(DesideredChoice) as DesideredChoice, DateTime"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND Type = '"+type+"'"
				+ " AND DateTime in (SELECT MAX(DateTime)"
											+" FROM ControlArrivalData)"
				+ " GROUP BY DateTime";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				ControlFlexibilityData data =  new ControlFlexibilityData(
						idControlAgent, cal, rs.getDouble("LowerLimit"), 
						rs.getDouble("UpperLimit"), rs.getDouble("CostKwh"), 
						rs.getDouble("DesideredChoice"), type);
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int countMessagesReceived (String idControlAgent)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM ControlArrivalData"
    			+ " WHERE IdControlAgent = '"+idControlAgent+"'"
    			+ " AND DateTime in (SELECT Max(DateTime)" 
											+"FROM ControlArrivalData)";
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
	
	public Boolean updateControlArrivalData (String idControlAgent, String type, boolean confirmed)
	{
		String query = "UPDATE ControlArrivalData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND Type = '"+type+"'"
				+ " AND DateTime IN (SELECT MAX(DateTime)"
									+ "	FROM ControlData)";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getLastConfirmedByChoice (String idAgent, boolean confirmed)
	{
		String query = "SELECT COUNT(*) as Count"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idAgent+"'"
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
