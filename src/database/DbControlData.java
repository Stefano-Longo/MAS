package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.ControlData;
import basicData.DerData;
import utils.GeneralData;

public class DbControlData extends DbConnection {

	DateFormat format = new GeneralData().getFormat();
	
	public Boolean addControlData (ControlData controlData)
	{
		String query = "INSERT INTO ControlData (IdAgent, IdPlatform, DateTime, DerPower, BatteryPower, LoadPower, GridPower, CostKwh, Confirmed)"
				+ " VALUES ('"+controlData.getIdAgent()+"','"+controlData.getIdPlatform()+"','"
						+format.format(controlData.getDatetime().getTime())+"',"+controlData.getDerPower()+","
						+controlData.getBatteryPower()+","+controlData.getLoadPower()+", "+controlData.getGridPower()+", "
						+controlData.getCostKwh()+", "+controlData.getConfirmed()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ControlData getLastControlDatabyIdAgent(String idAgent)
	{
		ControlData controlData = null;
		String query = "SELECT *"
				+ " FROM ControlData"
				+ " WHERE IdAgent = '"+idAgent+"'"
				+ " AND DateTime IN (SELECT MAX(DateTime)"
									+ "	FROM ControlData)";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getDate("DateTime"));
				controlData = new ControlData(idAgent, rs.getString("IdPlatform"), cal,
						rs.getDouble("DerPower"), rs.getDouble("BatteryPower"), rs.getDouble("LoadPower"),
						 rs.getDouble("GridPower"),  rs.getDouble("CostKwh"), rs.getInt("Confirmed"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return controlData;
	}
	
	public Boolean setConfirmed (String idAgent)
	{
		String query = "UPDATE ControlData"
				+ " SET Confirmed = 'true'"
				+ " WHERE IdAgent = '"+idAgent+"'"
				+ " AND DateTime IN (SELECT MAX(DateTime)"
									+ "	FROM ControlData)";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
