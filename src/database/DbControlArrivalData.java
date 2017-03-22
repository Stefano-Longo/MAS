package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.FlexibilityData;
import utils.GeneralData;

public class DbControlArrivalData extends DbConnection {

	DateFormat format = GeneralData.getFormat();

	public Boolean addControlArrivalData (String idControlAgent, FlexibilityData data)
	{
		String query = "INSERT INTO ControlArrivalData (IdControlAgent, DateTime,"
				+ " LowerLimit, UpperLimit, CostKwh, DesiredChoice, Type)"
				+ " VALUES ('"+idControlAgent+"','"+format.format(data.getDatetime().getTime())+"',"
				+data.getLowerLimit()+","+ data.getUpperLimit()+","+data.getCostKwh()+","
				+data.getDesiredChoice()+",'"+data.getType()+"')";
		//System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	public FlexibilityData getLastControlArrivalData (String idControlAgent, String type, Calendar datetime)
	{
		String query = "SELECT SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit, AVG(CostKwh) as CostKwh,"
				+ " SUM(DesiredChoice) as DesiredChoice, DateTime"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND Type = '"+type+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
				+ " GROUP BY DateTime";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				FlexibilityData data =  new FlexibilityData(
						idControlAgent, cal, GeneralData.round(rs.getDouble("LowerLimit"),2), 
						GeneralData.round(rs.getDouble("UpperLimit"), 2), 
						GeneralData.round(rs.getDouble("CostKwh"), 2), 
						rs.getDouble("DesiredChoice"), type);
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	public int countMessagesReceived (String idControlAgent, Calendar datetime)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM ControlArrivalData"
    			+ " WHERE IdControlAgent = '"+idControlAgent+"'"
    			+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		
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
	
	public Boolean updateControlArrivalData (String idControlAgent, String type, int confirmed, Calendar datetime)
	{
		String query = "UPDATE ControlArrivalData"
				+ " SET Confirmed = '"+confirmed+"'"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND Type = '"+type+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return false;
	}
	
	public int getLastConfirmedByChoice (String idAgent, int confirmed, Calendar datetime)
	{
		String query = "SELECT COUNT(*) as Count"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idAgent+"'"
				+ " AND Confirmed = '"+confirmed+"'"
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
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
