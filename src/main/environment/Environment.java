package main.environment;

import main.concept.EnvironmentalState;

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
