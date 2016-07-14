package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import basicData.BatteryData;
import utils.GeneralData;

public class DbBatteryData extends DbConnection	{

	DateFormat format = new GeneralData().format;

	public Boolean addBatteryData(BatteryData battery)
	{
		String query = "INSERT INTO BatteryDataHistory (IdBattery, DateTime, SocObjective, Soc, CostKwh, InputPowerMax,"
				+ " OutputPowerMax, PowerRequested, Confirmed)"
				+ " VALUES ('"+battery.getIdBattery()+"','"+format.format(battery.getDatetime().getTime())+"',"
						+battery.getSocObjective()+","+battery.getSoc()+","+battery.getCostKwh()+","
						+battery.getInputPowerMax()+","+battery.getOutputPowerMax()+","
						+battery.getPowerRequested()+", false)";
		System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean updateBatteryData(BatteryData battery)
	{
		String query = "UPDATE BatteryDataHistory"
				+ " SET SocObjective="+battery.getSocObjective()+", Soc="+battery.getSoc()+","
					+ " CostKwh="+battery.getCostKwh()+", PowerRequested="+battery.getPowerRequested()+","
					+ " Confirmed=true"
				+ " WHERE IdBattery = '"+battery.getIdBattery()+"'"
				+ " AND DateTime = '"+format.format(battery.getDatetime().getTime())+"'";
		System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
						rs.getDouble("OutputPowerMax"), rs.getDouble("PowerRequested"), rs.getDouble("DesideredChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

}
