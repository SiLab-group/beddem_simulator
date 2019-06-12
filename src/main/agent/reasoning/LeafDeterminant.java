package main.agent.reasoning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.concept.Option;
import main.concept.Task;

/**
 * The base node in decision making model. In which user has to define how agent
 * values an option based on its internal state and the task at hand.
 * 
 * @author khoa_nguyen
 * @see IAgent
 *
 */
public abstract class LeafDeterminant extends Determinant {

	public LeafDeterminant(String id, double weight) {
		super(id, weight);
	}

	/**
	 * Evaluate (give a value) to an option according to the task to be performed.
	 * This function usually implemented in an agent.
	 * 
	 * @param opt
	 *            The input to be evaluate.
	 * @param task
	 *            A task to be performed by the agent.
	 * @return The value of the option given by the agent.
	 */
	protected abstract double evalOpt(Option opt, Task task);

	@Override
	protected Map<Double, Set<Option>> evalOpts(Set<Option> inputOpts, Task task) {
		Map<Double, Set<Option>> result = new HashMap<Double, Set<Option>>();
		// For each options in the input set, evaluate it then put it in the result map.
		for (Option opt : inputOpts) {
			double newValue = evalOpt(opt, task);
			if (result.containsKey(newValue)) {
				result.get(newValue).add(opt);
			} else {
				HashSet<Option> opts = new HashSet<Option>();
				opts.add(opt);
				result.put(newValue, opts);
			}
		}
		return result;
	}

}
