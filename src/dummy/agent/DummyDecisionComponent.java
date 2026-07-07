package dummy.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import dummy.concept.MobilityOption;
import framework.agent.core.DecisionComponent;
import framework.agent.reasoning.Determinant;
import framework.agent.reasoning.TIBModel;
import framework.concept.Option;
import framework.concept.Task;

public class DummyDecisionComponent extends TIBModel implements DecisionComponent {

	private static Logger LOGGER = Logger.getLogger(DummyDecisionComponent.class.getName());

	// Expected utility per mode for each trip, keyed by task executing time.
	// Recorded so the reporter can export the model's real decision output.
	private final Map<Double, Map<String, Double>> euByTime = new ConcurrentHashMap<Double, Map<String, Double>>();

	public DummyDecisionComponent(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
			Determinant self_concept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(belief, evaluation, norm, role, self_concept, emotion, facilitatingCond, freq, attitudeWeight,
				socialWeight, affectWeight, intentionWeight, habitWeight);

	}

	@Override
	public Map<Double, Set<Option>> evaluateOptions(Set<Option> options, Task task) {
		// The behaviour-output utility per option. We use evalOpts (the raw
		// aggregate) rather than rankOptions here: children are already
		// normalised at every level inside the tree, and the thesis does NOT
		// apply a further normalisation to the top-level behaviour output.
		LOGGER.log(Level.DEBUG, "Evaluate options " + options.toString());
		Map<Double, Set<Option>> result = evalOpts(options, task);
		// Record the expected utility (map key) of each option for this trip.
		Map<String, Double> perMode = new HashMap<String, Double>();
		for (Map.Entry<Double, Set<Option>> entry : result.entrySet()) {
			for (Option opt : entry.getValue()) {
				perMode.put(((MobilityOption) opt).getMainVehicle().getName(), entry.getKey());
			}
		}
		this.euByTime.put(task.getExecutingTime(), perMode);
		return result;
	}

	/** Expected utility per mode for each trip (task executing time -&gt; mode -&gt; EU). */
	public Map<Double, Map<String, Double>> getEuByTime() {
		return this.euByTime;
	}

}
