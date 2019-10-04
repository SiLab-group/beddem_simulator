package framework.agent.core;

import java.util.Map;

import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;

public interface MemoryComponent {
	/**
	 * Provide the current internal state of the agent. The information/properties
	 * storing in InternalState can be included in the implementation of
	 * InternalState Interface.
	 * 
	 * @return The current internal state of the agent.
	 */
	InternalState getInternalState();

	/**
	 * Update the internal state of the agent using information provided from the
	 * feedback.
	 * @param task The task to be performed. 
	 * @param option
	 *            The option chosen by the agent.
	 * @param feedback
	 *            The information/properties needed to update the internal state.
	 *            included in the implementation of Feedback Interface.
	 */
	void updateInternalState(Task task, Option option, Feedback feedback);

	Map<Task, Option> getDecisionResults();


}
