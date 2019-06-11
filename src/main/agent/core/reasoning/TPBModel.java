package main.agent.core.reasoning;

public class TPBModel extends ParentDeterminant {
	public TPBModel(Determinant attitude, Determinant norm, Determinant behaviouralControl, double attitudeWeight,
			double behaviouralControlWeight, double normWeight, double intentionWeight) {
		super("FishbeinAndAjzen", 1);
		ParentDeterminant intention = new ParentDeterminant("intention", intentionWeight);
		
		addDeterminantChild(behaviouralControl);
		addDeterminantChild(intention);

		intention.addDeterminantChild(norm);
		intention.addDeterminantChild(attitude);
		intention.addDeterminantChild(behaviouralControl);

	}
}
