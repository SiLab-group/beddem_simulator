package dummy.concept;

import java.util.logging.Level;
import java.util.logging.Logger;

import framework.concept.Task;


// TODO: Needs to be implemented!!
public class MobilityTask extends Task {

	private static Logger LOGGER = Logger.getLogger(MobilityTask.class.getName());
	private double timeLimit;
	// private double n_Passengers;
	private double distance;
	private double timeStart;
	private double purpose;
	

	public MobilityTask(double executingTime, double timeStart, double distance, double purpose, double timeLimit) {
		super(executingTime);
		this.timeStart = timeStart;
		this.purpose = purpose;
		this.timeLimit = timeLimit;
		
		LOGGER.log(Level.INFO, "New event is initialised: " + toString());

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
	
	public double getPurpose() {
		return this.purpose;
	}
	
	public double getTimeStart() {
		return this.timeStart;
	}
	
	@Override
	public String toString() {
		String result = "TransportTask: ";
		result += ", starting time: " + this.executingTime;
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (other instanceof MobilityTask) {
			MobilityTask otherTask = (MobilityTask) other;
			return this.timeStart == otherTask.getTimeStart();
		}
		return false;

	}
}
