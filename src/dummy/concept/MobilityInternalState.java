package dummy.concept;

import java.util.Set;

import framework.concept.InternalState;

public class MobilityInternalState implements InternalState {
	private double currentFund;
	private Set<Vehicle> ownVehicles;

	public MobilityInternalState(double currentFund, Set<Vehicle> ownVehicles) {
		this.currentFund = currentFund;
		this.ownVehicles = ownVehicles;
	}

	public double getCurrentFund() {
		return this.currentFund;
	}

	public Set<Vehicle> getOwnVehicles() {
		return this.ownVehicles;
	}
}
