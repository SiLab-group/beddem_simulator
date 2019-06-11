package main.agent.core;

import java.util.List;

import framework.environment.ILocation;

/**
 * All agents must implement this interface so that it the simulation knows how
 * to step them.
 * 
 * @author khoa_nguyen
 *
 */
public interface IAgent {

	/**
	 * Controls the agent. This method will be called by the scheduler once per
	 * iteration.
	 * 
	 * @throws Exception
	 */
	void step() throws Exception;

	/**
	 * Used by Agents as a means of stating whether or not they can be run in
	 * parallel (i.e. there is no inter-agent communication which will make
	 * parallelisation non-trivial). If all the agents in a simulation return true,
	 * and the computer running the simulation has more than one core, it is
	 * possible to step agents simultaneously.
	 * 
	 * @return
	 */
	boolean isThreadable();

	/**
	 * (Optional). Add objects to the agents memory. Used to keep a record of all
	 * the buildings that they have passed.
	 * 
	 * @param <T>
	 * @param objects
	 *            The objects to add to the memory.
	 * @param clazz
	 *            The type of object.
	 */
	<T> void addToMemory(List<T> objects, Class<T> clazz);

	/**
	 * Get the current location of agent.
	 * 
	 * @return The current location of the agent.
	 */
	ILocation getLoc();

	/**
	 * Get the ID of the agent.
	 * 
	 * @return The ID of the agent.
	 */
	String getID();

	void updateInternalState();

}
