package main.agent.core;

import java.util.HashSet;
import java.util.Set;

import main.concept.Option;
import main.concept.Resource;
import main.environment.ILocation;

public abstract class MemoryComponent {
	
	// Set of available resource available to the agent.
	protected Set<Resource> resources;
	
	public MemoryComponent() {
		this.resources = new HashSet<Resource>();
	}

	/**
	 * Update the agent's internal state.
	 * 
	 * @param pickedOption
	 *            The best option that the agent selected
	 */
	protected abstract void updateCurrentState(Option pickedOpt);

	public abstract void update(ILocation loc);

	public void update(CommunicationComponent communicationComponent) {
		Option pickedOpt = communicationComponent.getPickedOpt();
		updateCurrentState(pickedOpt);
	}
	
}
