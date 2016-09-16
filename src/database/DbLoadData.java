package database;

import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.LoadData;
import utils.GeneralData;

public class DbLoadData extends DbConnection {

	DateFormat format = GeneralData.getFormat();
	
	public Boolean addLoadData(LoadData load)
	{
		if(load.getToDatetime() != null && load.getDatetime().getTime().compareTo(load.getToDatetime().getTime()) == 0){
			load.setCostKwh(1);
			Toolkit.getDefaultToolkit().beep();
			System.out.println("CIAO \n CIAO\n\n\n\nCIAO\nCIAO\n\n");
			System.out.println("\nDatetime: "+load.getDatetime().getTime()+" toDatetime: "+load.getToDatetime().getTime()+"\n");
		}
		String toDatetime = load.getToDatetime()==null ? null : "'"+format.format(load.getToDatetime().getTime())+"'";
		System.out.println("\nDatetime: "+load.getDatetime().getTime()+" toDatetime: "+toDatetime+"\n");
		String query = "INSERT INTO LoadDataHistory (IdLoad, DateTime, CostKwh, CriticalConsumption, NonCriticalConsumption,"
				+ " ConsumptionMin, ConsumptionMax, PowerRequested, DesideredChoice, ConsumptionShifted,"
				+ " ToDateTime, Confirmed)"
				+ " VALUES ("+load.getIdLoad()+",'"+format.format(load.getDatetime().getTime())+"',"
						+load.getCostKwh()+","+load.getCriticalConsumption()+","
						+load.getNonCriticalConsumption()+","+load.getConsumptionMin()+","+load.getConsumptionMax()+","
						+load.getPowerRequested()+","+load.getDesideredChoice()+","+load.getConsumptionShifted()+","
						+toDatetime+", 'false')";
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
					+ " ConsumptionShifted="+load.getConsumptionShifted()+", "
					+ " Confirmed = 'true'"
				+ " WHERE IdLoad = "+load.getIdLoad()
				+ " AND DateTime = '"+format.format(load.getDatetime().getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public LoadData getLastLoadData (int idLoad, Calendar datetime)
	{
		LoadData data = new LoadData();
		String query = "SELECT *"
				+ " FROM LoadDataHistory"
				+ " WHERE IdLoad = "+idLoad
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				Calendar cal1 = null;
				if(rs.getTimestamp("ToDateTime") != null)
				{
					cal1 = Calendar.getInstance();
					cal1.setTime(rs.getTimestamp("ToDateTime"));
				}
				data = new LoadData(rs.getInt("IdLoad"), cal, rs.getDouble("CostKwh"), 
						rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
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
