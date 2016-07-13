package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import basicData.BatteryData;

public class DbBatteryData extends DbConnection	{

	public Boolean addBatteryData(BatteryData battery)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(battery.getDatetime().getTime());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String query = "INSERT INTO BatteryDataHistory (Id, DateTime, SocObjective, Soc, CostKwh, InputPowerMax,"
				+ " OutputPowerMax, PowerRequested)"
				+ " VALUES ('"+battery.getIdBattery()+"','"+format.format(cal.getTime())+"',"+battery.getSocObjective()+","
						+battery.getSoc()+","+battery.getCostKwh()+","+battery.getInputPowerMax()+","
						+battery.getOutputPowerMax()+","+battery.getPowerRequested()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BatteryData getLastBatteryData (int idBattery)
	{
		BatteryData data = null;
		String query = "SELECT TOP 1 *"
				+ " FROM BatteryDataHistory"
				+ " WHERE RTRIM(IdBattery) = "+idBattery
				+ " ORDER BY DateTime DESC";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getDate("DateTime"));

				data = new BatteryData(rs.getInt("IdBattery"), cal, rs.getDouble("SocObjective"),
						rs.getDouble("Soc"), rs.getDouble("CostKwh"), rs.getDouble("InputPowerMax"),
						rs.getDouble("OutputPowerMax"), rs.getDouble("PowerRequested"));
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

}
