package dummy.report;

import java.util.Map;

import dummy.agent.StandardDummyAgent;
import dummy.concept.MobilityOption;
import dummy.concept.MobilityTask;
import dummy.context.AgentContext;
import dummy.environment.Location;
import framework.agent.core.IAgent;
import framework.concept.Option;
import framework.concept.Task;

/**
 * Decide output information of the simulator.
 * 
 * @author Khoa Nguyen
 */
public class DummyReporter implements IReporter {
	private AgentContext agentContext;

	public DummyReporter(AgentContext agentContext) {
		this.agentContext = agentContext;
	}

	@Override
	public String printReport() {
		String reportString = "\nagentID,location,start_time,km,vehicle\n";
		for (IAgent agent : this.agentContext) {
			StandardDummyAgent mobilityAgent = (StandardDummyAgent) agent;
			String location = ((Location) mobilityAgent.getLoc()).getName();
			Map<Task, Option> results = mobilityAgent.getDecisionResults();
			for (Task task : results.keySet()) {
				MobilityOption mobilityOption = (MobilityOption) results.get(task);
				MobilityTask mobilityTask = (MobilityTask) task;
				reportString += agent.getID() + "," + location + "," + mobilityTask.getExecutingTime() + ","
						+ mobilityTask.getDistance() + "," + mobilityOption.getMainVehicle().getName() + "\n";
			}
		}

		return reportString;
	}
}
