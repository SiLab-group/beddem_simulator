package dummy.agent;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import dummy.concept.MobilityEnvironmentalState;
import dummy.concept.MobilityInternalState;
import dummy.concept.MobilityOption;
import dummy.concept.MobilityTask;
import dummy.concept.Vehicle;
import dummy.environment.Location;
import framework.agent.core.PerceptionComponent;
import framework.concept.EnvironmentalState;
import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

public class DummyPerceptionComponent implements PerceptionComponent {

	private static Logger LOGGER = Logger.getLogger(DummyPerceptionComponent.class.getName());

	@Override
	public Set<Option> generateOptions(Task task, EnvironmentalState environmentalState,
			InternalState internalState) {
		Set<Vehicle> accessileVehicles = new HashSet<Vehicle>();
		MobilityTask mobilityTask = (MobilityTask) task;
		MobilityEnvironmentalState mobilityEnvStat = (MobilityEnvironmentalState) environmentalState;
		accessileVehicles.addAll(mobilityEnvStat.getPublicTransports());
		MobilityInternalState mobilityInternalStat = (MobilityInternalState) internalState;
		accessileVehicles.addAll(mobilityInternalStat.getOwnVehicles());

		Set<Option> opts = new HashSet<Option>();
		for (Vehicle vehicle : accessileVehicles) {
			double time = mobilityTask.getDistance()/vehicle.getSpeed();
			if (time < mobilityTask.getTimeLimit()) {
				double cost = vehicle.getCostPerKm()*mobilityTask.getDistance();
				if (cost<=mobilityInternalStat.getCurrentFund()) {
					opts.add(new MobilityOption(vehicle,cost,time));
				}
			}
		}

		return opts;
	}

}
