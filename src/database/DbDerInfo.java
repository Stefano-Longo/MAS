package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;

import basicData.DerInfo;
import utils.GeneralData;

public class DbDerInfo extends DbConnection {
	
	DateFormat format = GeneralData.getFormat();

	public DerInfo getDerInfoByIdAgent (String idAgent)
	{
		DerInfo data = new DerInfo();
		String query = "SELECT *"
					+ " FROM Der"
					+ " WHERE RTRIM(IdAgent) = '"+idAgent+"'";
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
		} finally {
			connClose();
		}
		return data;
	}

	public DerInfo getDerByIdDer(String identificator) 
	{
		DerInfo data = new DerInfo();
		String query = "SELECT *"
					+ " FROM Der"
					+ " WHERE IdDer = '"+identificator+"'";
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
		} finally {
			connClose();
		}
		return data;
	}
}
