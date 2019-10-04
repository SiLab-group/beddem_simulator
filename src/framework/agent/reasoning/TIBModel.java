package framework.agent.reasoning;

/**
 * A class represent Triandis' decision making model. User needs to define all
 * base determinants of the model, which can be further extended to reflect
 * other aspects of psychology.
 * 
 * @author khoa_nguyen
 *
 */
public class TIBModel extends ParentDeterminant {

	public TIBModel(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
			Determinant self_concept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super("Triandis", 1);
		ParentDeterminant intention = new ParentDeterminant("intention", intentionWeight);
		ParentDeterminant habits = new ParentDeterminant("habits", habitWeight);

		ParentDeterminant attitude = new ParentDeterminant("attitude", affectWeight);
		ParentDeterminant socialFactors = new ParentDeterminant("social", socialWeight);
		ParentDeterminant affect = new ParentDeterminant("affect", affectWeight);

		addDeterminantChild(intention);
		addDeterminantChild(habits);
		addDeterminantChild(facilitatingCond);

		habits.addDeterminantChild(freq);

		intention.addDeterminantChild(affect);
		intention.addDeterminantChild(socialFactors);
		intention.addDeterminantChild(attitude);

		affect.addDeterminantChild(emotion);

		socialFactors.addDeterminantChild(norm);
		socialFactors.addDeterminantChild(role);
		socialFactors.addDeterminantChild(self_concept);

		attitude.addDeterminantChild(belief);
		attitude.addDeterminantChild(evaluation);
	}

}
