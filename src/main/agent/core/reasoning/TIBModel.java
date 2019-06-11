package main.agent.core.reasoning;

/**
 * A class represent Triandis' decision making model. User needs to define all
 * base determinants of the model, which can be further extended to reflect
 * other aspects of psychology.
 * 
 * @author khoa_nguyen
 *
 */
public class TIBModel extends ParentDeterminant {

	public TIBModel(Determinant facilitatingCon, Determinant freq, Determinant emotion, Determinant norm, Determinant role,
			Determinant selfConcept, Determinant belief, Determinant evaluation, double attitudeWeight,
			double socialWeight, double affectWeight, double intentionWeight, double habitWeight, double facilitatingWeight) {
		super("Triandis", 1);
		ParentDeterminant intention = new ParentDeterminant("intention", intentionWeight);
		ParentDeterminant habits = new ParentDeterminant("habits", habitWeight);
		
		ParentDeterminant attitude = new ParentDeterminant("attitude", affectWeight);
		ParentDeterminant socialFactors = new ParentDeterminant("social", socialWeight);
		ParentDeterminant affect = new ParentDeterminant("affect", affectWeight);

		addDeterminantChild(intention);
		addDeterminantChild(habits);
		addDeterminantChild(facilitatingCon);

		habits.addDeterminantChild(freq);

		intention.addDeterminantChild(affect);
		intention.addDeterminantChild(socialFactors);
		intention.addDeterminantChild(attitude);

		affect.addDeterminantChild(emotion);

		socialFactors.addDeterminantChild(norm);
		socialFactors.addDeterminantChild(role);
		socialFactors.addDeterminantChild(selfConcept);

		attitude.addDeterminantChild(belief);
		attitude.addDeterminantChild(evaluation);
	}

}
