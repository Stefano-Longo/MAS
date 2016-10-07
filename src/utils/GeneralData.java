package utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GeneralData {

	private int timeSlot = 3600; //seconds 
	static private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ITALIAN);
	private double dieselKwhPrice = 1; //expressed in €
	private double priceKwhSold = 0.15; //expressed in €
	private double meanKwhPrice = 0.4;
	static private double sellEnergyPrice = 0.05; //expressed in €
	
	public int getTimeSlot() {
		return timeSlot;
	}
	
	public void setTimeSlot(int timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	static public DateFormat getFormat() {
		return format;
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
	
	static public double getSellEnergyPrice() {
		return sellEnergyPrice;
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

	static public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
