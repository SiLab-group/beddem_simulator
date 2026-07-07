package dummy.agent;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import dummy.concept.MobilityEnvironmentalState;
import dummy.concept.MobilityInternalState;
import dummy.concept.MobilityOption;
import dummy.concept.MobilityTask;
import dummy.concept.Vehicle;
import framework.agent.core.PerceptionComponent;
import framework.concept.EnvironmentalState;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;

public class DummyPerceptionComponent implements PerceptionComponent {

	private static Logger LOGGER = Logger.getLogger(DummyPerceptionComponent.class.getName());
	private String agentID;

	public DummyPerceptionComponent(String agentID) {
		super();
		this.agentID = agentID;
	}

	@Override
	public Set<Option> generateOptions(Task task, EnvironmentalState environmentalState, InternalState internalState) {
		Set<Vehicle> accessileVehicles = new HashSet<Vehicle>();
		MobilityTask mobilityTask = (MobilityTask) task;
		// Public transport available at the trip's origin.
		accessileVehicles.addAll(mobilityTask.getOriginTransports());
		MobilityInternalState mobilityInternalStat = (MobilityInternalState) internalState;
		accessileVehicles.addAll(mobilityInternalStat.getOwnVehicles());
		String debugStr = "Task for agent" + this.agentID + "Task distance: " + mobilityTask.getDistance()
				+ " Task maxtime: " + mobilityTask.getTimeLimit() + "\n";
		LOGGER.log(Level.DEBUG, debugStr);

		Set<Option> opts = new HashSet<Option>();
		for (Vehicle vehicle : accessileVehicles) {
			double time = mobilityTask.getDistance() / vehicle.getSpeed();
			if (time < mobilityTask.getTimeLimit()) {
				double cost = vehicle.getCostPerKm() * mobilityTask.getDistance();
				LOGGER.log(Level.DEBUG, "For vehicle " + vehicle.getName() + ": Time me is less " + time
						+ " than timelimit " + mobilityTask.getTimeLimit() + " Cost is " + cost);
				if (cost <= mobilityInternalStat.getCurrentFund()) {
					opts.add(new MobilityOption(vehicle, cost, time));
				}
			}
		}

		return opts;
	}

}
