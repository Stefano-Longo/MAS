package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import basicData.BatteryData;
import utils.GeneralData;

public class DbBatteryData extends DbConnection	{

	DateFormat format = GeneralData.getFormat();

	public Boolean addBatteryData(BatteryData battery)
	{
		String query = "INSERT INTO BatteryDataHistory (IdBattery, DateTime, SocObjective, Soc, CostKwh, InputPowerMax,"
				+ " OutputPowerMax, PowerRequested, DesiredChoice, Confirmed)"
				+ " VALUES ('"+battery.getIdBattery()+"','"+format.format(battery.getDatetime().getTime())+"',"
						+battery.getSocObjective()+","+battery.getSoc()+","+battery.getCostKwh()+","
						+battery.getInputPowerMax()+","+battery.getOutputPowerMax()+","
						+battery.getPowerRequested()+", "+battery.getDesiredChoice()+", '0')";
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
	
	public Boolean updateBatteryData(BatteryData battery)
	{
		String query = "UPDATE BatteryDataHistory"
				+ " SET SocObjective = "+battery.getSocObjective()+", Soc="+battery.getSoc()+","
					+ " CostKwh = "+battery.getCostKwh()+", PowerRequested="+battery.getPowerRequested()+","
					+ " Confirmed = 1"
				+ " WHERE IdBattery = "+battery.getIdBattery()
				+ " AND DateTime = '"+format.format(battery.getDatetime().getTime())+"'";
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
	
	public BatteryData getLastBatteryData (int idBattery, Calendar datetime)
	{
		BatteryData data = new BatteryData();
		/*String querysqlserver = "SELECT TOP 1 *"
				+ " FROM BatteryDataHistory"
				+ " WHERE IdBattery = "+idBattery
				+ " AND DateTime < '"+format.format(datetime.getTime())+"'"
				+ " ORDER BY DateTime DESC";*/
		String query = "SELECT *"
				+ " FROM BatteryDataHistory"
				+ " WHERE IdBattery = "+idBattery
				+ " AND DateTime < '"+format.format(datetime.getTime())+"'"
				+ " ORDER BY DateTime DESC"
				+ " LIMIT 1";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				data = new BatteryData(rs.getInt("IdBattery"), cal, rs.getDouble("SocObjective"),
						rs.getDouble("Soc"), rs.getDouble("CostKwh"), rs.getDouble("InputPowerMax"),
						rs.getDouble("OutputPowerMax"), rs.getDouble("PowerRequested"), rs.getDouble("DesiredChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return data;
	}

	public BatteryData getBatteryDataByDatetime (int idBattery, Calendar datetime)
	{
		BatteryData data = new BatteryData();
		String query = "SELECT *"
				+ " FROM BatteryDataHistory"
				+ " WHERE IdBattery = "+idBattery
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
				+ " ORDER BY DateTime DESC";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				data = new BatteryData(rs.getInt("IdBattery"), cal, rs.getDouble("SocObjective"),
						rs.getDouble("Soc"), rs.getDouble("CostKwh"), rs.getDouble("InputPowerMax"),
						rs.getDouble("OutputPowerMax"), rs.getDouble("PowerRequested"), rs.getDouble("DesiredChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return data;
	}
}
