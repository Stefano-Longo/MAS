package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basicData.ControlFlexibilityData;

public class DbControlArrivalData extends DbConnection {


	public Boolean addControlArrivalData (ControlFlexibilityData data)
	{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String query = "INSERT INTO ControlArrivalData (IdControlAgent, Type, AnalysisDateTime,"
				+ " DateTime, LowerLimit, UpperLimit, CostKwh, DesideredChoice)"
				+ " VALUES ('"+data.getIdAgent()+"',"+data.getType()+",'"
				+ format.format(data.getAnalysisDatetime().getTime())+"','"
				+ format.format(data.getDatetime().getTime())+"',"+data.getLowerLimit()+","
				+ data.getUpperLimit()+","+data.getCostKwh()+","+data.getDesideredChoice()+")";
		try {
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<ControlFlexibilityData> getControlArrivalDatabyType (String idControlAgent, String type)
	{
		ArrayList<ControlFlexibilityData> list = new ArrayList<ControlFlexibilityData>();
		String query = "SELECT *"
				+ " FROM ControlArrivalData"
				+ " WHERE IdControlAgent = '"+idControlAgent+"'"
				+ " AND Type = '"+type+"'"
				+ " AND AnalysisDateTime in (SELECT MAX(AnalysisDateTime)"
											+" FROM BatteryAggregatorData";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(rs.getDate("AnalysisDateTime"));
				
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rs.getDate("DateTime"));
				
				ControlFlexibilityData data = new ControlFlexibilityData(
						rs.getString("IdControlAgent"), rs.getString("Type"), cal1, cal2,
						rs.getDouble("LowerLimit"), rs.getDouble("UpperLimit"), 
						rs.getDouble("CostKwh"), rs.getDouble("DesideredChoice"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public int countMessagesReceived (String idControlAgent)
	{
		String query = "SELECT COUNT(*) as Count"
    			+ " FROM ControlArrivalData"
    			+ " WHERE IdAgent = '"+idControlAgent+"'"
    			+ " AND AnalysisDateTime in (SELECT Max(AnalysisDateTime)" 
											+"FROM ControlArrivalData)";
		try{
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				return rs.getInt("Count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
