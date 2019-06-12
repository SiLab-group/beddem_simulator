package main.agent.core;

import main.concept.Option;
import main.concept.Task;
import main.environment.ILocation;

public abstract class CommunicationComponent {
	
	private Option pickedOpt;
		
	/**
	 * Send update information to the environment
	 * 
	 * @param pickedOption
	 *            The best option that the agent selected
	 */
	protected abstract void sendUpdateToEnvironment(Task task, ILocation loc, Option pickedOpt, MemoryComponent memoryComponent);

	public void update(Task task, ILocation loc, DecisionComponent decisionComponent, MemoryComponent memoryComponent) {
		pickedOpt = decisionComponent.getPickedOpt();
		sendUpdateToEnvironment(task, loc, pickedOpt, memoryComponent);
	}

	public Option getPickedOpt() {
		return this.pickedOpt;
	}
	
}
