package framework.agent.core;

import java.util.Set;

import framework.concept.EnvironmentalState;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;

public interface PerceptionComponent {

	/**
	 * Function to be implemented that takes current environmental (external) state
	 * and internal state and generate the list of options for the tasks. The
	 * information/properties storing in EnvironmentalState and InternalState can be
	 * included in the implementation of IEnvironmentalState and InternalState.
	 * Interfaces respectively.
	 * 
	 * @param environmentalState
	 *            Information of current state of the environment (to be
	 *            implemented).
	 * @param internalState
	 *            Information of the internal state of the agent, which is stored in
	 *            memory (to be implemented).
	 * @return List of available options to perform the task.
	 */
	Set<Option> generateOptions(Task task, EnvironmentalState environmentalState,
			InternalState internalState);
}
