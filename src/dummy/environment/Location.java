package dummy.environment;

import java.util.Set;

import dummy.concept.MobilityEnvironmentalState;
import dummy.concept.Vehicle;
import main.concept.EnvironmentalState;
import main.environment.Environment;

public class Location implements Environment {

	private Set<Vehicle> publicTransports;
	private String id;

	public Location(String id, Set<Vehicle> publicTransports) {
		this.id = id;
		this.publicTransports = publicTransports;
	}

	@Override
	public EnvironmentalState getEnvironmentalState() {
		return new MobilityEnvironmentalState(publicTransports);
	}

}
