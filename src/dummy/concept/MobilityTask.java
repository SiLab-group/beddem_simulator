package dummy.concept;

import framework.concept.Task;


// TODO: Needs to be implemented!!
public class MobilityTask extends Task {

	private double timeLimit;
	// private double n_Passengers;
	private double distance;

	public MobilityTask(double executingTime) {
		super(executingTime);

	}

	public double getTimeLimit() {
		return this.timeLimit;
	}

//	public double getNPassengers() {
//		return this.n_Passengers;
//	}

	public double getDistance() {
		return this.distance;
	}
}
