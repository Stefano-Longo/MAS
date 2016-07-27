package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GeneralData {

	private int timeSlot = 3600; //seconds 
	private DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private double dieselKwhPrice = 1; //expressed in €
	private double priceKwhSold = 0.15; //expressed in €
	private double meanKwhPrice = 0.2;
	
	public int getTimeSlot() {
		return timeSlot;
	}
	
	public void setTimeSlot(int timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	public DateFormat getFormat() {
		return format;
	}
	
	public void setFormat(DateFormat format) {
		this.format = format;
	}
	
	public double getDieselKwhPrice() {
		return dieselKwhPrice;
	}
	
	public void setDieselKwhPrice(double dieselKwhPrice) {
		this.dieselKwhPrice = dieselKwhPrice;
	}

	public double getPriceKwhSold() {
		return priceKwhSold;
	}

	public void setPriceKwhSold(double priceKwhSold) {
		this.priceKwhSold = priceKwhSold;
	}
	
	public double getMeanKwhPrice() {
		return meanKwhPrice;
	}

	public void setMeanKwhPrice(double meanKwhPrice) {
		this.meanKwhPrice = meanKwhPrice;
	}

	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
