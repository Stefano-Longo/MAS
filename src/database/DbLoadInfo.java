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

	DateFormat format = GeneralData.getFormat();

	public ArrayList<LoadInfoPrice> getLoadInfoPricebyIdAgent (String idAgent, Calendar datetime)
	{
		ArrayList<LoadInfoPrice> list = new ArrayList<LoadInfoPrice>();
		String query = "SELECT *" 
					+ " FROM (Load as A JOIN LoadInfo as B ON A.IdLoad = B.IdLoad)"
						+ " JOIN LoadManagement as C ON B.Id = C.IdLoadDateTime"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'"
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'"
					+ " AND NonCriticalConsumption > 0"
					+ " ORDER BY DateTime";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getTimestamp("DateTime"));
				LoadInfoPrice data = new LoadInfoPrice(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						cal1, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getTimestamp("ToDateTime"));
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
					+ " FROM LoadInfo A JOIN Load B ON A.IdLoad = B.IdLoad "
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'"
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new LoadInfo(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						datetime, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public LoadInfo getLoadInfoByIdLoad (int idLoad, Calendar datetime)
	{
		LoadInfo data = null;
		String query = "SELECT *"
					+ " FROM LoadInfo as A JOIN Load as B ON A.IdLoad = B.IdLoad"
					+ " WHERE A.IdLoad = "+idLoad
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new LoadInfo(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						datetime, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public Boolean updateLoadInfo(LoadInfo loadInfo)
	{
		String datetime = loadInfo.getDatetime() == null ? null : format.format(loadInfo.getDatetime().getTime());

		String query = "UPDATE LoadInfo"
				+ " SET  ConsumptionAdded="+loadInfo.getConsumptionAdded()
				+ " WHERE IdLoad = '"+loadInfo.getIdLoad()+"'";
		if(datetime == null)
			query += " AND DateTime = null";
		else 
			query += " AND DateTime = '"+format.format(loadInfo.getDatetime().getTime())+"'";

		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
