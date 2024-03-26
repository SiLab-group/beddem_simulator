package framework.environment;

import framework.concept.AgentInfo;
import framework.concept.Option;
import framework.concept.EnvironmentalState;
import framework.concept.Feedback;
import framework.concept.Task;

/**
 * The location object where agents reside in.
 * 
 * @author khoa_nguyen
 *
 */
public interface Environment {
	/**
	 * Reset all the counters that accumulate the results.
	 */
	public EnvironmentalState getEnvironmentalState();
}
