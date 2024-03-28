package dummy.context;

import dummy.simulator.GlobalVars;
import framework.agent.core.IAgent;
import repast.simphony.context.DefaultContext;

/**
 * Container for all the agents
 * 
 * @author khoa_nguyen
 *
 */
public class AgentContext extends DefaultContext<IAgent> {

	public AgentContext() {
		super(GlobalVars.CONTEXT_NAMES.AGENT_CONTEXT);
	}

}
