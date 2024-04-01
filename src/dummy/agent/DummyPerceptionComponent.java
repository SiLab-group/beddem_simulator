package dummy.agent;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dummy.concept.MobilityEnvironmentalState;
import dummy.concept.MobilityInternalState;
import dummy.concept.MobilityOption;
import dummy.concept.MobilityTask;
import dummy.concept.Vehicle;
import framework.agent.core.PerceptionComponent;
import framework.concept.EnvironmentalState;
import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

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
		MobilityEnvironmentalState mobilityEnvStat = (MobilityEnvironmentalState) environmentalState;
		accessileVehicles.addAll(mobilityEnvStat.getPublicTransports());
		MobilityInternalState mobilityInternalStat = (MobilityInternalState) internalState;
		accessileVehicles.addAll(mobilityInternalStat.getOwnVehicles());
		String debugStr = "Task for agent" + this.agentID + "Task distance: " + mobilityTask.getDistance()
				+ " Task maxtime: " + mobilityTask.getTimeLimit() + "\n";
		LOGGER.log(Level.FINER, debugStr);

		Set<Option> opts = new HashSet<Option>();
		for (Vehicle vehicle : accessileVehicles) {
			double time = mobilityTask.getDistance() / vehicle.getSpeed();
			if (time < mobilityTask.getTimeLimit()) {
				double cost = vehicle.getCostPerKm() * mobilityTask.getDistance();
				LOGGER.log(Level.FINER, "For vehicle " + vehicle.getName() + ": Time me is less " + time
						+ " than timelimit " + mobilityTask.getTimeLimit() + " Cost is " + cost);
				if (cost <= mobilityInternalStat.getCurrentFund()) {
					opts.add(new MobilityOption(vehicle, cost, time));
				}
			}
		}

		return opts;
	}

	@Override
	public Feedback getFeedback(Task task, Option pickedOption, Environment environment) {
		return null;
	}

}
