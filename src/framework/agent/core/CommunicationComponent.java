package framework.agent.core;

import java.util.Map;
import java.util.Set;

import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

public interface CommunicationComponent {

	/**
	 * Pick an option from this list of evaluated options. Agent can pick either the
	 * highest/lowest value (deterministic) or based on the probabilities produced
	 * from these values (stochastic).
	 * 
	 * @param evaluatedOptions
	 *            List of values and all the options that scored that value after
	 *            the evaluation process.
	 * @return An option selected by the agent.
	 */
	Option pickOption(Map<Double, Set<Option>> evaluatedOptions);

	/**
	 * Get feedback from the environment about the action performed. The
	 * information/properties storing in Feedback can be included in the
	 * implementation of Feedback.
	 * 
	 * @param task
	 *            The current task that the agent wants to perform.
	 * @param pickedOption
	 *            The action that was chosen by the agent.
	 * @param internalState
	 *            The agent's parameter or agenda.
	 * @param loc
	 *            The environment where the agent lives.
	 * @return The feedback from the environment about the action performed. (To be
	 *         implemented)
	 */
	Feedback getFeedback(Task task, Option pickedOption, InternalState internalState, Environment loc);
}
