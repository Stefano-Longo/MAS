package database;

import java.sql.SQLException;

import basicData.ControlData;

public class DbControlData extends DbConnection {

	public Boolean addControlData (ControlData controlData)
	{
		String query = "INSERT INTO ControlData (IdAgent, IdPlatform, DateTime, DerPower, BatteryPower, LoadPower)"
				+ " VALUES ('"+controlData.getIdAgent()+"','"+controlData.getIdPlatform()+"',"
						+controlData.getDatetime()+",'"+controlData.getDerPower()+"',"
						+controlData.getBatteryPower()+","+controlData.getLoadPower()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
