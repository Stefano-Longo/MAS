package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.ControlFlexibilityData;
import utils.GeneralData;

public class DbControlArrivalData extends DbConnection {

	DateFormat format = new GeneralData().getFormat();

	public Boolean addControlArrivalData (ControlFlexibilityData data)
	{
		String query = "INSERT INTO ControlArrivalData (IdControlAgent, DateTime,"
				+ " LowerLimit, UpperLimit, CostKwh, DesideredChoice, Type)"
				+ " VALUES ('"+data.getIdAgent()+"','"+format.format(data.getDatetime().getTime())+"',"
				+data.getLowerLimit()+","+ data.getUpperLimit()+","+data.getCostKwh()+","
				+data.getDesideredChoice()+",'"+data.getType()+"')";
		System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ControlFlexibilityData getLastControlArrivalData (String idControlAgent)
	{
		String query = "SELECT SUM(LowerLimit) as LowerLimit, SUM(UpperLimit) as UpperLimit, AVG(CostKwh) as CostKwh,"
				+ " SUM(DesideredChoice) as DesideredChoice, DateTime"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND DateTime in (SELECT MAX(DateTime)"
											+" FROM ControlArrivalData)"
				+ " GROUP BY DateTime";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				ControlFlexibilityData data =  new ControlFlexibilityData(
						idControlAgent, cal, rs.getDouble("LowerLimit"), 
						rs.getDouble("UpperLimit"), rs.getDouble("CostKwh"), 
						rs.getDouble("DesideredChoice"), rs.getString("Type"));
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
}
