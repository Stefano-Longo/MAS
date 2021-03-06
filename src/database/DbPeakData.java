package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import utils.GeneralData;
import basicData.FlexibilityData;
import basicData.PeakData;

public class DbPeakData extends DbConnection {

	DateFormat format = GeneralData.getFormat();
	
	public Boolean updatePeak (PeakData peaks)
	{
		String query = "UPDATE Peak SET PeakValue = "+peaks.getPeakValue()
				+ " WHERE DateTime = '"+format.format(peaks.getDatetime().getTime())+"'";
		//System.out.println(query);
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connClose();
		return false;
	}
	
	public Boolean addPeaks (ArrayList<PeakData> peaks)
	{
		for(int i=0; i<peaks.size(); i++)
		{
			String query = "INSERT INTO Peak (IdAggregatorAgent, DateTime, PeakValue)"
					+ " VALUES ('"+peaks.get(i).getIdAggregatorAgent()+"','"+format.format(peaks.get(i).getDatetime().getTime())+"',"
					+ peaks.get(i).getPeakValue()+")";
			//System.out.println(query);
			try {
				stmt.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connClose();
		return false;
	}
	
	public ArrayList<PeakData> getTodayPeaks (String idAggregatorAgent, Calendar datetime)
	{
		ArrayList<PeakData> list = new ArrayList<PeakData>();
		/*String querysqlserver = "SELECT *"
				+ " FROM Peak"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND DATEPART(DAY, DateTime) = "+datetime.get(Calendar.DAY_OF_MONTH);*/
		String query = "SELECT *"
				+ " FROM Peak"
				+ " WHERE IdAggregatorAgent = '"+idAggregatorAgent+"'"
				+ " AND DAY(DateTime) = "+datetime.get(Calendar.DAY_OF_MONTH);
		//System.out.println(query);
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(rs.getTimestamp("DateTime"));
				
				PeakData data = new PeakData(idAggregatorAgent, cal, rs.getDouble("PeakValue"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connClose();
		}
		return list;
	}
}
