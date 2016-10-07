package basicData;

public class DerInfo {

	private int idDer;
	private String idAgent;
	private String platform;
	private double productionMax;
	private String type;
	private double usageMin;
	private double usageMax;
	private double capitalCost;
	private double maintenanceCost;
	private double totalKwh;

	public DerInfo(int idDer, String idAgent, String platform, double productionMax, String type, 
			double usageMin, double usageMax, double capitalCost, double maintenanceCost, double totalKwh) 
	{
		this.idDer = idDer;
		this.idAgent = idAgent;
		this.platform = platform;
		this.productionMax = productionMax;
		this.type = type;
		this.usageMin = usageMin;
		this.usageMax = usageMax;
		this.capitalCost = capitalCost;
		this.maintenanceCost = maintenanceCost;
		this.totalKwh = totalKwh;
	}
	
	public DerInfo() { }

	public int getIdDer() {
		return idDer;
	}

	public void setIdLoad(int idDer) {
		this.idDer = idDer;
	}

	public String getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(String idAgent) {
		this.idAgent = idAgent;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public double getProductionMax() {
		return productionMax;
	}

	public void setProductionMax(double productionMax) {
		this.productionMax = productionMax;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getUsageMin() {
		return usageMin;
	}

	public void setUsageMin(double usageMin) {
		this.usageMin = usageMin;
	}

	public double getUsageMax() {
		return usageMax;
	}

	public void setUsageMax(double usageMax) {
		this.usageMax = usageMax;
	}

	public double getCapitalCost() {
		return capitalCost;
	}

	public void setCapitalCost(double capitalCost) {
		this.capitalCost = capitalCost;
	}

	public double getMaintenanceCost() {
		return maintenanceCost;
	}

	public void setMaintenanceCost(double maintenanceCost) {
		this.maintenanceCost = maintenanceCost;
	}

	public double getTotalKwh() {
		return totalKwh;
	}

	public void setTotalKwh(double totalKwh) {
		this.totalKwh = totalKwh;
	}

	public void setIdDer(int idDer) {
		this.idDer = idDer;
	}
	
}
