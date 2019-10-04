package dummy.concept;

import java.util.Set;

import framework.concept.EnvironmentalState;

public class MobilityEnvironmentalState implements EnvironmentalState {

	private Set<Vehicle> publicTransports;

	public MobilityEnvironmentalState(Set<Vehicle> publicTransports) {
		this.publicTransports = publicTransports;
	}

	public Set<Vehicle> getPublicTransports() {
		return this.publicTransports;
	}
}
