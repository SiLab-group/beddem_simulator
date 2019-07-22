package main.agent.core;

import java.util.Set;

import main.concept.EnvironmentalState;
import main.concept.Feedback;
import main.concept.InternalState;
import main.concept.Option;
import main.concept.Task;
import main.environment.Environment;

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

	/**
	 * Get feedback from the environment about the action performed. The
	 * information/properties storing in Feedback can be included in the
	 * implementation of Feedback.
	 * 
	 * @param task
	 *            The current task that the agent wants to perform.        
	 * @param pickedOption
	 *            The action that was chosen by the agent.
	 * @param environment
	 *            The environment where the agent lives.
	 * @return The feedback from the environment about the action performed. (To be
	 *         implemented)
	 */
	Feedback getFeedback(Task task, Option pickedOption, Environment environment);
}
