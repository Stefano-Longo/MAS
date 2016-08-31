package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.TimePowerPrice;
import utils.GeneralData;

public class DbGridData extends DbConnection {

	DateFormat format = GeneralData.getFormat();
	
	public  Boolean addPriceData (ArrayList<TimePowerPrice> priceData)
	{
		for(int i=0; i < priceData.size(); i++)
		{
			String query = "INSERT INTO Price (AnalysisDateTime, DateTime, Threshold, EnergyPrice)"
					+ " VALUES ('"+format.format(priceData.get(0).getDatetime().getTime())
								+"','"+format.format(priceData.get(i).getDatetime())+"',"
								+priceData.get(i).getThreshold()+","+priceData.get(i).getEnergyPrice()+")";
			try {
				stmt.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public ArrayList<TimePowerPrice> getPriceData (Calendar data)
	{
		ArrayList<TimePowerPrice> prices = new ArrayList<TimePowerPrice>();
		String query = "SELECT *"
				+ " FROM Price"
				+ " WHERE AnalysisDateTime = '"+format.format(data.getTime())+"'"
				+ " ORDER BY DateTime";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));

				TimePowerPrice price = new TimePowerPrice(cal, 
						rs.getDouble("Threshold"), rs.getDouble("EnergyPrice"));
				prices.add(price);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prices;
	}
}
