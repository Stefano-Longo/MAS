package database;

import java.sql.ResultSet;
import java.sql.SQLException;

import basicData.BatteryInfo;

public class DbBatteryInfo extends DbConnection{

	public Boolean addBattery(BatteryInfo battery)
	{
		String query = "INSERT INTO Battery (IdAgent, IdPlatform, Capacity, Type, BatteryInputMax,"
				+ " BatteryOutputMax, SocMin, SocMax)"
				+ " VALUES ('"+battery.getIdAgent()+"','"+battery.getIdPlatform()+"',"
						+battery.getCapacity()+",'"+battery.getType()+"',"+battery.getBatteryInputMax()+","
						+battery.getBatteryOutputMax()+","+battery.getSocMin()+","+battery.getSocMax()+")";
		//System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	
	public BatteryInfo getBatteryInfoByIdAgent (String idAgent)
	{
		String query = "SELECT *"
				+ " FROM Battery"
				+ " WHERE IdAgent = '"+idAgent+"'";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){ 
				BatteryInfo batteryInfo = new BatteryInfo(rs.getInt("IdBattery"), rs.getString("IdAgent"), 
						rs.getString("IdPlatform"), rs.getDouble("Capacity"), rs.getString("Type"), 
						rs.getDouble("BatteryInputMax"), rs.getDouble("BatteryOutputMax"), rs.getDouble("SocMin"), 
						rs.getDouble("SocMax"), rs.getDouble("CapitalCost"), rs.getDouble("MaintenaceCost"), 
						rs.getDouble("CyclesNumber"), rs.getDouble("RoundTripEfficiency"));
				return batteryInfo;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
	
	public BatteryInfo getBatteryInfoByIdBattery (String idBattery)
	{
		String query = "SELECT *"
				+ " FROM Battery"
				+ " WHERE IdBattery = '"+idBattery+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				BatteryInfo batteryInfo = new BatteryInfo(rs.getInt("IdBattery"), rs.getString("IdAgent"), 
						rs.getString("IdPlatform"), rs.getDouble("Capacity"), rs.getString("Type"), 
						rs.getDouble("BatteryInputMax"), rs.getDouble("BatteryOutputMax"), rs.getDouble("SocMin"), 
						rs.getDouble("SocMax"), rs.getDouble("CapitalCost"), rs.getDouble("MaintenaceCost"), 
						rs.getDouble("CyclesNumber"), rs.getDouble("RoundTripEfficiency"));
				return batteryInfo;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return null;
	}
}
