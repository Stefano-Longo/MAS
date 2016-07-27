package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.LoadData;
import utils.GeneralData;

public class DbLoadData extends DbConnection {

	DateFormat format = new GeneralData().getFormat();
	
	public Boolean addLoadData(LoadData load)
	{
		String query = "INSERT INTO LoadDataHistory (IdLoad, DateTime, CostKwh, CriticalConsumption, NonCriticalConsumption,"
				+ " ConsumptionMin, ConsumptionMax, PowerRequested, DesideredChoice, ConsumptionShifted,"
				+ " ToDateTime, Confirmed)"
				+ " VALUES ("+load.getIdLoad()+",'"+format.format(load.getDatetime().getTime())+"',"
						+load.getCostKwh()+","+load.getCriticalConsumption()+","
						+load.getNonCriticalConsumption()+","+load.getConsumptionMin()+","+load.getConsumptionMax()+","
						+load.getPowerRequested()+","+load.getDesideredChoice()+","+load.getConsumptionShifted()+",'"
						+format.format(load.getToDatetime().getTime())+"', 'false')";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean updateLoadData(LoadData load)
	{
		String query = "UPDATE LoadDataHistory"
				+ " SET  PowerRequested="+load.getPowerRequested()+","
					+ " ConsumptionShifted="+load.getConsumptionShifted()
					+ " ToDateTime="+format.format(load.getToDatetime().getTime())
					+ " Confirmed = true"
				+ " WHERE Id = '"+load.getIdLoad()+"'"
				+ " AND DateTime = '"+format.format(load.getDatetime().getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public LoadData getLastLoadData (int idLoad)
	{
		LoadData data = null;
		String query = "SELECT TOP 1 *"
				+ " FROM LoadDataHistory"
				+ " WHERE RTRIM(IdBattery) = "+idLoad
				+ " ORDER BY DateTime DESC";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getTimestamp("ToDateTime"));
				data = new LoadData(rs.getInt("IdBattery"), cal, rs.getDouble("CostKwh"), 
						rs.getDouble("CriticalDataConsumption"), rs.getDouble("NonCriticalDataConsumption"), 
						rs.getDouble("ConsumptionMin"), rs.getDouble("ConsumptionMax"), 
						rs.getDouble("PowerRequested"), rs.getDouble("DesideredChoice"), 
						rs.getDouble("ConsumptionShifted"), cal1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
}
