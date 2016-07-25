package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import utils.GeneralData;

public class DbLoadInfo extends DbConnection {

	DateFormat format = new GeneralData().getFormat();

	public ArrayList<LoadInfoPrice> getLoadInfoPricebyIdAgent (String idAgent, Calendar datetime)
	{
		ArrayList<LoadInfoPrice> list = new ArrayList<LoadInfoPrice>();
		String query = "SELECT *" //subquery prende i dati del giorno e ora subito dopo now
					+ " FROM Load as A JOIN LoadManagement as B ON A.Id = B.IdLoadDateTime"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'"
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
					+ " ORDER BY DateTime";
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
	
	public LoadInfo getLoadInfoByIdAgent (String idAgent, Calendar datetime)
	{
		LoadInfo data = null;
		String query = "SELECT *"
					+ " FROM Load"
					+ " WHERE RTRIM(IdAgent) = "+idAgent
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new LoadInfo(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						datetime, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("toDateTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public LoadInfo getLoadInfoByIdLoad (int idLoad, Calendar datetime)
	{
		LoadInfo data = null;
		String query = "SELECT TOP 1 *"
					+ " FROM Load"
					+ " WHERE IdLoad = "+idLoad
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new LoadInfo(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						datetime, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("toDateTime"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public Boolean updateLoadInfo(int idLoad, Calendar toDateTime, double consumptionAdded)
	{
		String query = "UPDATE Load"
				+ " SET  ConsumptionAdded="+consumptionAdded
				+ " WHERE Id = '"+idLoad+"'"
				+ " AND ToDateTime = '"+format.format(toDateTime.getTime())+"'";
		System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
