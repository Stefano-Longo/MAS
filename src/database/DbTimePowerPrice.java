package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.TimePowerPrice;
import utils.GeneralData;

public class DbTimePowerPrice extends DbConnection {

	DateFormat format = GeneralData.getFormat();

	public TimePowerPrice getTimePowerPrice (Calendar datetime)
	{
		String query;
		if(datetime == null)
		{
			query = "SELECT TOP 1 *"
					+ " FROM Price"
					+ " ORDER BY DateTime";
		}
		else
		{
			query = "SELECT TOP 1 *" 
					+ " FROM Price"
					+ " WHERE DateTime > '"+format.format(datetime.getTime())+"'"
					+ " ORDER BY DateTime";
		}
		
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				TimePowerPrice data = new TimePowerPrice(cal, 
						rs.getDouble("Threshold"), rs.getDouble("EnergyPrice"));
				return data;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<TimePowerPrice> getDailyTimePowerPrice (Calendar datetime)
	{
		ArrayList<TimePowerPrice> list = new ArrayList<TimePowerPrice>();
		
		String query = "SELECT *" 
				+ " FROM Price"
				+ " WHERE DateTime >= '"+format.format(datetime.getTime())+"'"
				+ " AND DATEPART(DAY, DateTime) = "+datetime.get(Calendar.DAY_OF_MONTH)
				+ " ORDER BY DateTime";
		
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				TimePowerPrice data = new TimePowerPrice(cal, 
						rs.getDouble("Threshold"), rs.getDouble("EnergyPrice"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
