package dummy.environment;

import java.util.Set;

import dummy.concept.MobilityEnvironmentalState;
import dummy.concept.Vehicle;
import framework.concept.EnvironmentalState;
import framework.environment.Environment;

public class Location implements Environment {

	private Set<Vehicle> publicTransports;
	private String id;
	private String name;

	public Location(String id, String name, Set<Vehicle> publicTransports) {
		this.id = id;
		this.name = name;
		this.publicTransports = publicTransports;
	}

	@Override
	public EnvironmentalState getEnvironmentalState() {
		return new MobilityEnvironmentalState(publicTransports);
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

}
