 package main.agent.reasoning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import framework.concept.Option;
import framework.concept.Task;

/**
 * A class represent a standard determinant (psychology) in decision making.
 * User needs to define how agent would evaluate a set of option based on its
 * internal state and the task at hand.
 * 
 * @author khoa_nguyen
 *
 */
public abstract class Determinant {
	private static Logger LOGGER = Logger.getLogger(Determinant.class.getName());
	
	private String id;
	private double weight;

	public Determinant(String id, double weight) {
		this.id = id;
		this.weight = weight;
	}

	/**
	 * Rank the input options and put into a list. All options that has the same
	 * value are put into the same element set of the list.
	 * 
	 * @param inputOpts
	 *            The set of options that needed to be ranked.
	 * @param task
	 *            The task needed to be done by the agent performing one of the
	 *            option.
	 * @return List of set of option that organised according to their ranking. All
	 *         options that has the same value are put into the same element set of
	 *         the list.
	 */
	public Map<Double, Set<Option>> rankOptions(Set<Option> inputOpts, Task task) {
		Map<Double, Set<Option>> rankingResult = new HashMap<Double, Set<Option>>();
		Map<Double, Set<Option>> valueToOptsMap = evalOpts(inputOpts, task);
		double sumValue = 0;
		for (Double v : valueToOptsMap.keySet()) {
			sumValue += v;
		}
		if (Double.compare(sumValue, 0.0) == 0) sumValue = 1;
		String debugString = "Determinant: " + this.id + " with weight " + this.weight + " \n";
		
		for (Map.Entry<Double, Set<Option>> entry : valueToOptsMap.entrySet()) {
			debugString += "Value: " + entry.getKey() + " \n";
			for (Option opt : entry.getValue()) {
				debugString += "Option: " + opt.toString() + " \n";
			}
			debugString += " \n";
			rankingResult.put(entry.getKey()/sumValue,entry.getValue());
		}
		LOGGER.log(Level.FINE, debugString);
		return rankingResult;
	}
	
	/**
	 * This function evaluate the input option set and put product the result as a
	 * map from value to option(s).
	 * 
	 * @param inputOpts
	 *            The set of option to be evaluated.
	 * @param task
	 *            The task to be performed by the agent.
	 */
	protected abstract Map<Double, Set<Option>> evalOpts(Set<Option> inputOpts, Task task);
	
	public String getID() {
		return this.id;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
}
