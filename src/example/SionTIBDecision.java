package example;

import java.util.Map;
import java.util.Set;

import framework.agent.core.DecisionComponent;
import framework.agent.reasoning.Determinant;
import framework.agent.reasoning.LeafDeterminant;
import framework.agent.reasoning.ParentDeterminant;
import framework.agent.reasoning.TIBModel;
import framework.concept.Option;
import framework.concept.Task;

/**
 * TIB decision component for the Sion -> Sierre worked example, built on
 * beddem_simulator's own reasoning engine ({@link TIBModel} /
 * {@link ParentDeterminant} / {@link LeafDeterminant}).
 *
 * <p>Unlike {@code StandardDummyAgent} (where only time and cost were
 * implemented and the other determinants were stubbed), every TIB determinant
 * here has a real ranking function, so all input weights influence the
 * outcome. Each leaf returns a cost-like value where <b>lower = better</b>;
 * the engine normalises and weights these up the tree, and the option with the
 * lowest aggregated expected utility is the preferred one.</p>
 *
 * <p>Scenario data and weights come from the BedDeM paper, Table 1 (an 18 km
 * trip from Sion to Sierre with three modes: car, train, bike).</p>
 */
public class SionTIBDecision extends TIBModel implements DecisionComponent {

	public SionTIBDecision(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
			Determinant selfConcept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(belief, evaluation, norm, role, selfConcept, emotion, facilitatingCond, freq, attitudeWeight, socialWeight,
				affectWeight, intentionWeight, habitWeight);
	}

	@Override
	public Map<Double, Set<Option>> evaluateOptions(Set<Option> options, Task task) {
		// Return the root-level TIB aggregation (expected utility per option).
		// We use the raw aggregate rather than the extra top-level normalisation
		// that rankOptions applies, so the numbers reproduce the paper's Table 1
		// EU values (Car ~1.13, Train ~3.07, Bike ~4.81). The chosen option
		// (lowest EU) is identical either way, since normalisation is monotonic.
		return evalOpts(options, task);
	}

	/**
	 * Assemble a fully-wired TIB decision component from the Table 1 weights.
	 *
	 * @param evaluationWeight weight of the "evaluation" node grouping price and time
	 */
	public static SionTIBDecision build(double priceWeight, double timeWeight, double normWeight, double roleWeight,
			double selfConceptWeight, double emotionWeight, double facilitatingWeight, double freqWeight,
			double evaluationWeight, double attitudeWeight, double socialWeight, double affectWeight,
			double intentionWeight, double habitWeight) {

		ParentDeterminant evaluation = new ParentDeterminant("evaluation", evaluationWeight);
		evaluation.addDeterminantChild(leaf("price", priceWeight));
		evaluation.addDeterminantChild(leaf("time", timeWeight));

		Determinant belief = null; // not used in the Table 1 example
		Determinant norm = leaf("norm", normWeight);
		Determinant role = leaf("role", roleWeight);
		Determinant selfConcept = leaf("self", selfConceptWeight);
		Determinant emotion = leaf("emotion", emotionWeight);
		Determinant facilitating = leaf("facilitating", facilitatingWeight);
		Determinant freq = leaf("freq", freqWeight);

		return new SionTIBDecision(belief, evaluation, norm, role, selfConcept, emotion, facilitating, freq,
				attitudeWeight, socialWeight, affectWeight, intentionWeight, habitWeight);
	}

	/**
	 * One leaf determinant. Reads the matching cost-like value from the option
	 * (lower = better in every case).
	 */
	private static LeafDeterminant leaf(final String id, double weight) {
		return new LeafDeterminant(id, weight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				SionMobilityOption o = (SionMobilityOption) opt;
				switch (id) {
				case "price":        return o.getPrice();
				case "time":         return o.getTime();
				case "norm":         return o.getNorm();
				case "role":         return o.getRole();
				case "self":         return o.getSelfConcept();
				case "emotion":      return o.getComfort();
				case "facilitating": return o.getFacilitating();
				case "freq":         return o.getFreq();
				default:             return 0.0;
				}
			}
		};
	}
}
