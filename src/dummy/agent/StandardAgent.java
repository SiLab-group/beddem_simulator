package dummy.agent;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import main.agent.core.TaskExecutionAgent;
import main.environment.ILocation;

/**
 * A class represent a standard agent that has mobilityation demand.
 * 
 * @author khoa_nguyen
 *
 */
public abstract class StandardAgent extends TaskExecutionAgent {

	public StandardAgent(String id, ILocation loc) {
		super(id, loc);
		// TODO Auto-generated constructor stub
	}
	private DummyMemoryComponent mobilityMemory;
	private DummyCommunicationComponent mobilityCommunication;
	private TIBDecisionComponent mobilityDecision;

}
