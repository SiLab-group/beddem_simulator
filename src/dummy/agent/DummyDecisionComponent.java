package dummy.agent;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import main.agent.core.DecisionComponent;
import main.agent.reasoning.Determinant;
import main.agent.reasoning.TIBModel;
import main.concept.Option;

public class DummyDecisionComponent extends TIBModel implements DecisionComponent {

	private static Logger LOGGER = Logger.getLogger(DummyDecisionComponent.class.getName());
	
	public DummyDecisionComponent(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
			Determinant self_concept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(belief, evaluation, norm, role, self_concept, emotion, facilitatingCond, freq, attitudeWeight, socialWeight,
				affectWeight, intentionWeight, habitWeight);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<Double, Set<Option>> evaluateOptions(Set<Option> options) {
		// TODO Auto-generated method stub
		return null;
	}

}
