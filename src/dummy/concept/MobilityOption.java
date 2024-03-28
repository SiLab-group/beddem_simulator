package dummy.concept;

import framework.concept.Option;

public class MobilityOption implements Option {
	private Vehicle mainVehicle;
	private double cost;
	private double time;
	
	public MobilityOption (Vehicle mainVehicle, double cost, double time) {
		this.mainVehicle = mainVehicle;
		this.cost = cost;
		this.time = time;
	}
	
	public Vehicle getMainVehicle() {
		return this.mainVehicle;
	}
	
	public double getCost() {
		return this.cost;
	}

	public double getTime() {
		return this.time;
	}
}
