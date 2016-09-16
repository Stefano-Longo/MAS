package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;

import basicData.DerData;
import utils.GeneralData;

public class DbDerData extends DbConnection {

	DateFormat format = GeneralData.getFormat();

	public Boolean addDerData(DerData der)
	{
		String query = "INSERT INTO DerDataHistory (IdDer, DateTime, CostKwh, ProductionMin,"
				+ " ProductionMax, ProductionRequested, DesideredChoice, Confirmed)"
				+ " VALUES ('"+der.getIdDer()+"','"+format.format(der.getDatetime().getTime())+"',"
						+der.getCostKwh()+","+der.getProductionMin()+","+der.getProductionMax()+","
						+der.getProductionRequested()+","+der.getDesideredChoice()+", 'false')";
		System.out.println(query);
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Get the average of the production of the last month before today. The values took in consideration
	 * are the values of the same datetime as the one requested (the next one)
	 * 
	 * @param idDer
	 * @param datetime
	 * @return
	 */
	public DerData getAverageLastMonthProduction (int idDer, Calendar datetime)
	{
		DerData data = new DerData();
		String query = "SELECT Avg(CostKwh) as CostKwh, Avg(ProductionMin) as ProdMin,"
				+ " Avg(ProductionMax) as ProdMax, Avg(ProductionRequested) as ProdReq,"
				+ " Avg(DesideredChoice) as DesChoice"
				+ " FROM DerDataHistory"
				+ " WHERE IdDer = "+idDer
				+ " AND DATEPART(HOUR, Datetime) = "+datetime.get(Calendar.HOUR_OF_DAY)
				+ " AND DATEPART(MINUTE, Datetime) = "+datetime.get(Calendar.MINUTE);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new DerData(idDer, datetime, rs.getDouble("CostKwh"), rs.getDouble("ProdMin"),
						rs.getDouble("ProdMax"), rs.getDouble("ProdReq"), rs.getDouble("DesChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public DerData getLastDerData (int idDer, Calendar datetime)
	{
		DerData data = new DerData();
		String query = "SELECT *"
				+ " FROM DerDataHistory"
				+ " WHERE IdDer = "+idDer
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
	
				data = new DerData(idDer, cal, rs.getDouble("CostKwh"), rs.getDouble("ProductionMin"),
					rs.getDouble("ProductionMax"), rs.getDouble("ProductionRequested"), rs.getDouble("DesideredChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public Boolean updateDerData(DerData der)
	{
		String query = "UPDATE DerDataHistory"
				+ " SET ProductionRequested = "+der.getProductionRequested()+","
					+ " Confirmed = 'true'"
				+ " WHERE IdDer = "+der.getIdDer()
				+ " AND DateTime = '"+format.format(der.getDatetime().getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
