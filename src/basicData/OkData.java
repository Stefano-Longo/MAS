package basicData;

import java.io.Serializable;
import java.util.Calendar;

public class OkData implements Serializable {

	private Calendar datetime;
	private String type;
	private Boolean ok;
	
	public OkData(Calendar datetime, String type, Boolean ok) {
		this.datetime = datetime;
		this.type = type;
		this.ok = ok;
	}
	
	public Calendar getDatetime() {
		return datetime;
	}

	public void setDatetime(Calendar datetime) {
		this.datetime = datetime;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Boolean getOk() {
		return ok;
	}
	
	public void setOk(Boolean ok) {
		this.ok = ok;
	}
	
	
}
