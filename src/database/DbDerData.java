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
				+ " ProductionMax, ProductionRequested, DesiredChoice, Confirmed)"
				+ " VALUES ('"+der.getIdDer()+"','"+format.format(der.getDatetime().getTime())+"',"
						+der.getCostKwh()+","+der.getProductionMin()+","+der.getProductionMax()+","
						+der.getProductionRequested()+","+der.getDesiredChoice()+", '0')";
		//System.out.println(query);
		try {
			stmt.execute(query);
			connClose();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
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
	public DerData getAverageLastMonthProductionByIdDer (int idDer, Calendar datetime)
	{
		DerData data = new DerData();
		String querysqlserver = "SELECT Avg(CostKwh) as CostKwh, Avg(ProductionMin) as ProdMin,"
				+ " Avg(ProductionMax) as ProdMax, Avg(ProductionRequested) as ProdReq,"
				+ " Avg(DesiredChoice) as DesChoice"
				+ " FROM DerDataHistory"
				+ " WHERE IdDer = "+idDer
				+ " AND DATEPART(HOUR, Datetime) = "+datetime.get(Calendar.HOUR_OF_DAY)
				+ " AND DATEPART(MINUTE, Datetime) = "+datetime.get(Calendar.MINUTE)
				+ " AND DATEDIFF(day,DateTime,'"+format.format(datetime.getTime())+"') between 0 and 30";
		String query = "SELECT Avg(CostKwh) as CostKwh, Avg(ProductionMin) as ProdMin,"
				+ " Avg(ProductionMax) as ProdMax, Avg(ProductionRequested) as ProdReq,"
				+ " Avg(DesiredChoice) as DesChoice"
				+ " FROM DerDataHistory"
				+ " WHERE IdDer = "+idDer
				+ " AND HOUR(Datetime) = "+datetime.get(Calendar.HOUR_OF_DAY)
				+ " AND MINUTE(Datetime) = "+datetime.get(Calendar.MINUTE)
				+ " AND DATEDIFF('"+format.format(datetime.getTime())+"', DateTime) between 0 and 30";
		//System.out.println(query);
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				data = new DerData(idDer, datetime, rs.getDouble("CostKwh"), rs.getDouble("ProdMin"),
						rs.getDouble("ProdMax"), rs.getDouble("ProdReq"), rs.getDouble("DesChoice"));
			}
			connClose();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return data;
	}
	
	public double getAverageLastMonthProduction (Calendar datetime)
	{
		ResultSet rs = null;
		/*String querysqlserver = "SELECT AVG(q.Production) as ProductionAvg"
				+ " FROM (SELECT DateTime, Avg(DesiredChoice) as Production"
				+ " FROM DerDataHistory"
				+ " WHERE DATEPART(HOUR, Datetime) = "+datetime.get(Calendar.HOUR_OF_DAY)
					+ " AND DATEPART(MINUTE, Datetime) = "+datetime.get(Calendar.MINUTE)
					+ " AND DATEDIFF(day,DateTime,'"+format.format(datetime.getTime())+"') between 0 and 30"
				+ " GROUP BY DateTime) as q";*/
		String query = "SELECT SUM(q.Production) as ProductionAvg"
				+ " FROM (SELECT IdDer, Avg(DesiredChoice) as Production"
				+ " FROM DerDataHistory"
				+ " WHERE HOUR(Datetime) = "+datetime.get(Calendar.HOUR_OF_DAY)
					+ " AND MINUTE(Datetime) = "+datetime.get(Calendar.MINUTE)
					+ " AND DATEDIFF('"+format.format(datetime.getTime())+"',DateTime) between 0 and 30"
				+ " GROUP BY IdDer) as q";
		//System.out.println(query);
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				return rs.getDouble("ProductionAvg");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return 0;
	}
	
	public DerData getLastDerData (int idDer, Calendar datetime)
	{
		DerData data = new DerData();
		String query = "SELECT *"
				+ " FROM DerDataHistory"
				+ " WHERE IdDer = "+idDer
				+ " AND DateTime = '"+format.format(datetime.getTime())+"'";
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
	
				data = new DerData(idDer, cal, rs.getDouble("CostKwh"), rs.getDouble("ProductionMin"),
					rs.getDouble("ProductionMax"), rs.getDouble("ProductionRequested"), rs.getDouble("DesiredChoice"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return data;
	}
	
	public Boolean updateDerData(DerData der)
	{
		String query = "UPDATE DerDataHistory"
				+ " SET ProductionRequested = "+der.getProductionRequested()+","
					+ " Confirmed = 1"
				+ " WHERE IdDer = "+der.getIdDer()
				+ " AND DateTime = '"+format.format(der.getDatetime().getTime())+"'";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return false;
	}

}
