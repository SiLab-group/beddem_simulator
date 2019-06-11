package main.agent.core;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import framework.concept.Task;
import framework.environment.ILocation;

/**
 * Define all the fields and decision making process of an agent. Here the agent
 * perform the step() method which was scheduled by the ContextManager.
 * 
 * @author khoa_nguyen
 *
 */
public abstract class TaskExecutionAgent implements IAgent {

	private static Logger LOGGER = Logger.getLogger(TaskExecutionAgent.class.getName());

	protected String id;

	// Current agent's location.
	protected ILocation loc;

	// All the agent's scheduled tasks to be performed.
	private List<Task> schedule;

	protected DecisionComponent decisionComponent;
	protected CommunicationComponent communicationComponent;
	protected MemoryComponent memoryComponent;

	public TaskExecutionAgent(String id, ILocation loc) {
		this.id = id;
		this.loc = loc;
		this.schedule = new LinkedList<Task>();
		this.decisionComponent = createDecisionComponent();
		this.communicationComponent = createCommunicationComponent();
		this.memoryComponent = createMemoryComponent();
	}

	protected abstract DecisionComponent createDecisionComponent();
	protected abstract CommunicationComponent createCommunicationComponent();
	protected abstract MemoryComponent createMemoryComponent();

	@Override
	public void step() throws Exception {
		LOGGER.log(Level.FINE, "Agent " + this.id + " is stepping.");
		// Get the next event from schedule.
		Task task = schedule.remove(0);

		// Observe the current world and social groups.
		memoryComponent.update(loc);

		// Make decision based on perception and belief.
		decisionComponent.update(task, memoryComponent);
				
		// Communicate the decision to environment and the social groups.
		communicationComponent.update(task, loc, decisionComponent, memoryComponent);
		
		// Update the result to the decision to the internal memory of the agent.
		memoryComponent.update(communicationComponent);

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

	@Override
	public final boolean isThreadable() {
		return true;
	}

	@Override
	public ILocation getLoc() {
		return this.loc;
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
