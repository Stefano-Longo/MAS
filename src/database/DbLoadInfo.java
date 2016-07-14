package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.BatteryData;
import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import utils.GeneralData;

public class DbLoadInfo extends DbConnection {

	DateFormat format = new GeneralData().format;

	public ArrayList<LoadInfoPrice> getLoadInfoPricebyIdAgent (String idAgent)
	{
		ArrayList<LoadInfoPrice> list = new ArrayList<LoadInfoPrice>();
		Calendar cal = Calendar.getInstance();
		String query = "SELECT *"
					+ " FROM"
						+ "(SELECT TOP 1 *" //subquery prende i dati del giorno e ora subito dopo now
						+ " FROM Load"
						+ " WHERE RTRIM(IdAgent) = "+idAgent
						+ " AND DateTime > '"+format.format(cal.getTime())+"'"
						+ " ORDER BY DateTime) as A"
					+ " JOIN LoadManagement as B ON A.IdLoad = B.IdLoad";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("DateTime"));

				LoadInfoPrice data = new LoadInfoPrice(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						cal1, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("toDateTime"));
				
				data.setToDatetime(cal2);
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public LoadInfo getLoadInfoByIdAgent (String idAgent)
	{
		LoadInfo data = null;
		Calendar cal = Calendar.getInstance();
		String query = "SELECT TOP 1 *"
					+ " FROM Load"
					+ " WHERE RTRIM(IdAgent) = "+idAgent
					+ " AND DateTime > '"+format.format(cal.getTime())+"'"
					+ " ORDER BY DateTime";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("DateTime"));

				data = new LoadInfo(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						cal1, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("toDateTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public Boolean updateLoadInfo()
	{
		
	}
	
}
