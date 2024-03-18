package framework.agent.core;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import framework.concept.EnvironmentalState;
import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

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
	protected PerceptionComponent perceptionComponent;
	protected MemoryComponent memoryComponent;
	protected DecisionComponent decisionComponent;
	protected CommunicationComponent communicationComponent;

	public TaskExecutionAgent(String id, Environment loc) {
		this.id = id;
		this.loc = loc;
		this.schedule = new LinkedList<Task>();
	}

	protected void setup_overrides() {
		this.decisionComponent = createDecisionComponent();
		this.memoryComponent = createMemoryComponent();
		this.communicationComponent = createCommunicationComponent();
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
		LOGGER.log(Level.INFO, "Agent " + this.id + " is stepping.");
		// Get the next event from schedule.
		Task task = schedule.remove(0);
		LOGGER.log(Level.INFO, "After remove from shedule");
		EnvironmentalState environmentalState = loc.getEnvironmentalState();
		InternalState internalState = this.memoryComponent.getInternalState();
		LOGGER.log(Level.INFO,"After internal state");
		Set<Option> options = this.perceptionComponent.generateOptions(task, environmentalState, internalState);
		LOGGER.log(Level.INFO,"After option perception");
		Map<Double, Set<Option>> evaluatedOptions = this.decisionComponent.evaluateOptions(options,task);
		LOGGER.log(Level.INFO,"After evaluated options");
		Option pickedOption = this.communicationComponent.pickOption(evaluatedOptions);
		LOGGER.log(Level.INFO,"After picked option");
		Feedback feedback = this.communicationComponent.getFeedback(task, pickedOption, internalState, this.loc);
		LOGGER.log(Level.INFO,"After feedback");
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
		LOGGER.log(Level.INFO, "Inside of task "+ task.toString());
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
