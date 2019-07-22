package dummy.report;

import java.util.Set;

import dummy.context.AgentContext;
import dummy.context.LocationContext;

/**
 * Decide output information of the simulator.
 * 
 * @author Khoa Nguyen
 */
public class DummyReporter implements IReporter {
	private LocationContext locationContext;
	private AgentContext agentContext;
	private Set<String> vehicleCategories;

	public DummyReporter(LocationContext locationContext, AgentContext agentContext, Set<String> vehicleCategories) {
		this.locationContext = locationContext;
		this.vehicleCategories = vehicleCategories;
		this.agentContext = agentContext;
	}

	@Override
	public String printReport() {
		return null;
	}
}
