package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.LoadInfoPrice;
import utils.GeneralData;

public class DbLoadInfo extends DbConnection {

	DateFormat format = GeneralData.getFormat();

	public ArrayList<LoadInfoPrice> getLoadInfoPricebyIdAgent (String idAgent, Calendar datetime)
	{
		ArrayList<LoadInfoPrice> list = new ArrayList<LoadInfoPrice>();
		String query = "SELECT A.IdLoad, IdAgent, IdPlatform, B.DateTime, CriticalConsumption,"
						+ " NonCriticalConsumption, ConsumptionAdded, EnergyPrice, ToDateTime" 
					+ " FROM ((Loads as A JOIN LoadInfo as B ON A.IdLoad = B.IdLoad)"
						+ " JOIN LoadManagement as C ON B.Id = C.IdLoadDateTime)"
						+ " JOIN Price P on C.ToDateTime = P.DateTime"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'"
						+ " AND B.DateTime = '"+format.format(datetime.getTime())+"'"
						+ " AND NonCriticalConsumption > 0"
					+ " ORDER BY P.EnergyPrice";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getTimestamp("DateTime"));
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getTimestamp("ToDateTime"));
				LoadInfoPrice data = new LoadInfoPrice(rs.getInt("IdLoad"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						cal1, rs.getDouble("CriticalConsumption"), rs.getDouble("NonCriticalConsumption"), 
						rs.getDouble("ConsumptionAdded"), rs.getDouble("EnergyPrice"), cal2);
				list.add(data);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LoadInfo getLoadInfoByIdAgent (String idAgent, Calendar datetime)
	{
		LoadInfo data = new LoadInfo();
		String query = "SELECT *"
					+ " FROM LoadInfo A JOIN Loads B ON A.IdLoad = B.IdLoad"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'"
					+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		System.out.println(query);
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
	
	public LoadInfo getLoadInfoByIdLoad (String idLoad, Calendar datetime)
	{
		LoadInfo data = new LoadInfo();
		String query = "SELECT *"
					+ " FROM LoadInfo as A JOIN Loads as B ON A.IdLoad = B.IdLoad"
					+ " WHERE A.IdLoad = '"+idLoad+"'"
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
		String query = "UPDATE LoadInfo"
				+ " SET  ConsumptionAdded += "+loadInfo.getConsumptionAdded()
				+ " WHERE IdLoad = "+loadInfo.getIdLoad()
				+ " AND DateTime = '"+format.format(loadInfo.getDatetime().getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public ArrayList<FlexibilityData> getFutureLoadInfoByIdAgent (int idLoad, Calendar datetime)
	{
		ArrayList<FlexibilityData> list = new ArrayList<FlexibilityData>();
		String querysqlserver = "SELECT *"
				+ " FROM LoadInfo"
				+ " WHERE IdLoad = "+idLoad
				+ " AND DateTime > '"+format.format(datetime.getTime())+"'"
				+ " AND DATEPART(DAY, DateTime) = "+datetime.get(Calendar.DAY_OF_MONTH);
		String query = "SELECT *"
				+ " FROM LoadInfo"
				+ " WHERE IdLoad = "+idLoad
				+ " AND DateTime > '"+format.format(datetime.getTime())+"'"
				+ " AND DAYOFMONTH(DateTime) = "+datetime.get(Calendar.DAY_OF_MONTH);
		System.out.println("FUTURE LIST "+query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				double lowerLimit = rs.getDouble("CriticalConsumption")+rs.getDouble("ConsumptionAdded");
				double upperLimit = lowerLimit + rs.getDouble("NonCriticalConsumption");
				
				FlexibilityData data = new FlexibilityData(rs.getString("idLoad"), cal, lowerLimit,
						upperLimit, 0, upperLimit);
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
