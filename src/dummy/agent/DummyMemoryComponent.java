package dummy.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import dummy.concept.MobilityFeedback;
import dummy.concept.MobilityInternalState;
import dummy.concept.MobilityOption;
import dummy.concept.MobilityTask;
import dummy.concept.Vehicle;
import main.agent.core.MemoryComponent;
import main.concept.Feedback;
import main.concept.InternalState;
import main.concept.Option;
import main.concept.Task;

public class DummyMemoryComponent implements MemoryComponent {

	private static Logger LOGGER = Logger.getLogger(DummyMemoryComponent.class.getName());

	private double currentFund;
	private Set<Vehicle> ownVehicles;
	private Map<Vehicle, Double> lastExperience;
	private Map<Vehicle, Integer> pastFreq;
	private Map<Task, Option> decisionResults;

	public DummyMemoryComponent(double currentFund, Set<Vehicle> ownVehicles) {
		this.currentFund = currentFund;
		this.ownVehicles = ownVehicles;
		this.lastExperience = new HashMap<Vehicle, Double>();
		this.pastFreq = new HashMap<Vehicle, Integer>();
		this.decisionResults = new HashMap<Task, Option>();
	}
	
	@Override
	public InternalState getInternalState() {
		return new MobilityInternalState(currentFund, ownVehicles);
	}

	@Override
	public void updateInternalState(Task task, Option option, Feedback feedback) {
		MobilityFeedback mobilityFeedBack = (MobilityFeedback) feedback;
		MobilityOption mobilityOption = (MobilityOption) option;
		MobilityTask mobilityTask = (MobilityTask) task;
		this.currentFund -= mobilityFeedBack.getCost();
		Vehicle mainVehicle = mobilityOption.getMainVehicle();
		this.lastExperience.put(mainVehicle, mobilityFeedBack.getExperienceScore());
		if (!this.pastFreq.containsKey(mainVehicle)) {
			this.pastFreq.put(mainVehicle, 1);
		} else {
			this.pastFreq.put(mainVehicle, this.pastFreq.get(mainVehicle)+1);
		}
		this.decisionResults.put(mobilityTask, mobilityOption);
	}

	@Override
	public Map<Task, Option> getDecisionResults() {
		return this.decisionResults;
	}

	
}
