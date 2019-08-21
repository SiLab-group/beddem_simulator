package main.agent.core;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.concept.EnvironmentalState;
import main.concept.Feedback;
import main.concept.InternalState;
import main.concept.Opinion;
import main.concept.Option;
import main.concept.Task;
import main.environment.Environment;

/**
 * Define all the fields and decision making process of an agent. Here the agent
 * perform the step() method which was scheduled by the ContextManager.
 * 
 * @author khoa_nguyen
 * 
 *
 */
public abstract class TaskExecutionAgent implements IAgent {

	private static Logger LOGGER = Logger.getLogger(TaskExecutionAgent.class.getName());

	protected String id;

	// Current agent's location, which contains environmental information.
	protected Environment loc;

	// All the agent's scheduled tasks to be performed.
	private List<Task> schedule;

	// Internal components of the agent.
	private PerceptionComponent perceptionComponent;
	private MemoryComponent memoryComponent;
	private DecisionComponent decisionComponent;
	private CommunicationComponent communicationComponent;

	public TaskExecutionAgent(String id, Environment loc) {
		this.id = id;
		this.loc = loc;
		this.schedule = new LinkedList<Task>();
		this.decisionComponent = createDecisionComponent();
		this.communicationComponent = createCommunicationComponent();
		this.memoryComponent = createMemoryComponent();
		this.perceptionComponent = createPerceptionComponent();
	}

	/**
	 * Implementation of perception component.
	 * 
	 */
	protected abstract PerceptionComponent createPerceptionComponent();

	/**
	 * Implementation of memory component.
	 * 
	 */
	protected abstract MemoryComponent createMemoryComponent();

	/**
	 * Implementation of decision-making component.
	 * 
	 */
	protected abstract DecisionComponent createDecisionComponent();

	/**
	 * Implementation of communication component.
	 * 
	 */
	protected abstract CommunicationComponent createCommunicationComponent();

	@Override
	public void step() throws Exception {
		LOGGER.log(Level.FINE, "Agent " + this.id + " is stepping.");
		// Get the next event from schedule.
		Task task = schedule.remove(0);

		EnvironmentalState environmentalState = loc.getEnvironmentalState();
		InternalState internalState = this.memoryComponent.getInternalState();

		Set<Option> options = this.perceptionComponent.generateOptions(task, environmentalState, internalState);

		Map<Double, Set<Option>> evaluatedOptions = this.decisionComponent.evaluateOptions(options,task);

		Option pickedOption = this.communicationComponent.pickOption(evaluatedOptions);

		Feedback feedback = this.perceptionComponent.getFeedback(task, pickedOption, loc);

		this.memoryComponent.updateInternalState(task, pickedOption,feedback);
	}

	/**
	 * Add a task to agent's schedule. The schedule is sorted in the order of
	 * executing time of tasks.
	 * 
	 * @param task
	 *            The task needed to be add to agent's schedule.
	 */
	public void addToSchedule(Task task) {
		ListIterator<Task> scheduleIt = this.schedule.listIterator(0);
		if (!scheduleIt.hasNext()) {
			this.schedule.add(task);
		} else {
			while (scheduleIt.hasNext()) {
				Task taskInList = scheduleIt.next();
				if (taskInList.compareTo(task) > 0) {
					scheduleIt.previous();
					break;
				}
			}
			scheduleIt.add(task);
		}

	}

	/**
	 * Get this agent current location information.
	 * 
	 * @param task
	 *            The task needed to be add to agent's schedule.
	 */
	public Environment getLoc() {
		return this.loc;
	}

	public Opinion getOpinion(Option option, Task task) {
		return this.communicationComponent.getOpinion(option, task);
	}
	
	public Map<Task, Option> getDecisionResults() {
		return this.memoryComponent.getDecisionResults();
	}
	
	@Override
	public final boolean isThreadable() {
		return true;
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Agent " + this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!TaskExecutionAgent.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final TaskExecutionAgent other = (TaskExecutionAgent) obj;
		if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
