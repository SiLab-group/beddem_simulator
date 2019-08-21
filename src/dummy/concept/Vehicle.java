package dummy.concept;

public class Vehicle {
	private double costPerKm;
	private double speed;
	private String id;
	private String name;
	
	public Vehicle (String id, String name, double speed, double cost) {
		this.id = id;
		this.name = name;
		this.speed = speed;
		this.costPerKm = cost;
	}

	public double getCostPerKm() {
		return this.costPerKm;
	}

	public double getSpeed() {
		return this.speed;
	}
	
	public String getName() {
		return this.name;
	}
}
