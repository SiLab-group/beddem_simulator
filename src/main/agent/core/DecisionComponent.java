package main.agent.core;

import java.util.Map;
import java.util.Set;

import main.concept.Option;

public interface DecisionComponent {

	/**
	 * Evaluate all options in the provided set.
	 * 
	 * @param options
	 *            The options provided for evaluation.
	 * @return The map for values to all the options scored that value.
	 */
	Map<Double, Set<Option>> evaluateOptions(Set<Option> options);

}
