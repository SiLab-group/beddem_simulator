package dummy.agent;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import framework.agent.core.DecisionComponent;
import framework.agent.reasoning.Determinant;
import framework.agent.reasoning.TIBModel;
import framework.concept.Option;
import framework.concept.Task;

public class DummyDecisionComponent extends TIBModel implements DecisionComponent {

	private static Logger LOGGER = Logger.getLogger(DummyDecisionComponent.class.getName());

	public DummyDecisionComponent(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
			Determinant self_concept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(belief, evaluation, norm, role, self_concept, emotion, facilitatingCond, freq, attitudeWeight,
				socialWeight, affectWeight, intentionWeight, habitWeight);

	}

	@Override
	public Map<Double, Set<Option>> evaluateOptions(Set<Option> options, Task task) {
		// Get the list of all ranked options from agent's decision making model.
		LOGGER.log(Level.DEBUG, "Rank options" + options.toString());
		return rankOptions(options, task);
	}

}
