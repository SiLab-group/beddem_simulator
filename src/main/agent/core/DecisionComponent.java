package main.agent.core;

import java.util.Set;

import framework.concept.Option;
import framework.concept.Task;

public abstract class DecisionComponent {
	
	private Option pickedOpt;

	public void update(Task task, MemoryComponent memoryComponent) {
		// Create list of options for the agent and evaluate them.
		Set<Option> opts = calculatePossibleOptions(task, memoryComponent);

		// Pick an option from the list based on their probability and inform
		// the environment. Also put the task and its options to planning
		// result.
		pickedOpt = pickAnOpt(opts, task);
	}
	
	/**
	 * Check agent's desire with its internal state and environment constraints to
	 * generate possible list of options for that agent.
	 * 
	 * @param task
	 *            The task which the agent need to perform
	 * @param memoryComponent 
	 * @return Possible list of options for the agent to achieve goal
	 */
	protected abstract Set<Option> calculatePossibleOptions(Task task, MemoryComponent memoryComponent);

	protected abstract Option pickAnOpt(Set<Option> opts, Task task);

	public Option getPickedOpt() {
		return pickedOpt;
	}
	
}
