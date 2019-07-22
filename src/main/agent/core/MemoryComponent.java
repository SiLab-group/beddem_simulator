package main.agent.core;

import main.concept.Feedback;
import main.concept.InternalState;
import main.concept.Option;

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
	 * 
	 * @param option
	 *            The option chosen by the agent.
	 * @param feedback
	 *            The information/properties needed to update the internal state.
	 *            included in the implementation of Feedback Interface.
	 */
	void updateInternalState(Option option, Feedback feedback);
}
