package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.DerInfo;
import utils.GeneralData;

public class DbDerInfo extends DbConnection {
	
	DateFormat format = new GeneralData().getFormat();

	public DerInfo getDerInfoByIdAgent (String idAgent)
	{
		DerInfo data = null;
		String query = "SELECT TOP 1 *"
					+ " FROM Der"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new DerInfo(rs.getInt("IdDer"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						rs.getDouble("ProductionMax"), rs.getString("Type"), rs.getDouble("UsageMin"), 
						rs.getDouble("UsageMax"), rs.getDouble("CapitalCost"), rs.getDouble("MaintenanceCost"),
						rs.getDouble("TotalKwh"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	public DerInfo getDerByIdDer(int identificator) 
	{
		DerInfo data = null;
		String query = "SELECT TOP 1 *"
					+ " FROM Der"
					+ " WHERE IdDer = "+identificator
					+ " ORDER BY DateTime";
		System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getTimestamp("DateTime"));

				data = new DerInfo(rs.getInt("IdDer"), rs.getString("IdAgent"), rs.getString("IdPlatform"),
						rs.getDouble("ProductionMax"), rs.getString("Type"), rs.getDouble("UsageMin"), 
						rs.getDouble("UsageMax"), rs.getDouble("CapitalCost"), rs.getDouble("MaintenanceCost"),
						rs.getDouble("TotalKwh"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
}
