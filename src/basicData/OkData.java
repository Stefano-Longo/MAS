package basicData;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class OkData implements Serializable {

	private Calendar datetime;
	private String type;
	private int ok;
	
	public OkData(Calendar datetime, String type, int ok) {
		this.datetime = (Calendar)datetime.clone();
		this.type = type;
		this.ok = ok;
	}
	
	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = (Calendar)datetime.clone();
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getOk() {
		return ok;
	}
	
	public void setOk(int ok) {
		this.ok = ok;
	}
	
	
}
