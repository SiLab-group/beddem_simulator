package main.agent.core;

import java.util.Map;
import java.util.Set;

import main.concept.Opinion;
import main.concept.Option;
import main.concept.Task;

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
	 * Return an opinion for certain option to perform a task.
	 * 
	 * @return An opinion for certain option to perform a task.
	 */
	Opinion getOpinion(Option option, Task task);

}
