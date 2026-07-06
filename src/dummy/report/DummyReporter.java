package dummy.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

	private static Logger LOGGER = Logger.getLogger(DummyReporter.class.getName());
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
		writeDecisionsCsv();
		return reportString;
	}

	/**
	 * Write the full decision of every trip (expected utility of each option and
	 * which one was chosen) to output/decisions.csv. This is the model's own
	 * output; the analysis tools (analysis/plot_tib.py, analysis/tib_dashboard.html)
	 * read it rather than recomputing the decision.
	 */
	private void writeDecisionsCsv() {
		File dir = new File("output");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try (FileWriter w = new FileWriter(new File(dir, "decisions.csv"))) {
			w.write("agent,location,start_time,km,mode,eu,chosen\n");
			for (IAgent agent : this.agentContext) {
				StandardDummyAgent a = (StandardDummyAgent) agent;
				String location = ((Location) a.getLoc()).getName();
				Map<Double, Map<String, Double>> euByTime = a.getEuByTime();

				// From the chosen decisions: trip distance and the picked mode per trip.
				Map<Double, Double> kmByTime = new HashMap<Double, Double>();
				Map<Double, String> chosenByTime = new HashMap<Double, String>();
				for (Map.Entry<Task, Option> e : a.getDecisionResults().entrySet()) {
					MobilityTask t = (MobilityTask) e.getKey();
					kmByTime.put(t.getExecutingTime(), t.getDistance());
					chosenByTime.put(t.getExecutingTime(), ((MobilityOption) e.getValue()).getMainVehicle().getName());
				}

				for (Map.Entry<Double, Map<String, Double>> trip : euByTime.entrySet()) {
					double time = trip.getKey();
					Double km = kmByTime.get(time);
					String chosen = chosenByTime.get(time);
					for (Map.Entry<String, Double> mode : trip.getValue().entrySet()) {
						w.write(agent.getID() + "," + location + "," + time + "," + (km == null ? "" : km) + ","
								+ mode.getKey() + "," + mode.getValue() + ","
								+ (mode.getKey().equals(chosen) ? "1" : "0") + "\n");
					}
				}
			}
			LOGGER.log(Level.DEBUG, "Wrote output/decisions.csv");
		} catch (IOException ex) {
			LOGGER.log(Level.WARN, "Could not write output/decisions.csv", ex);
		}
	}
}
