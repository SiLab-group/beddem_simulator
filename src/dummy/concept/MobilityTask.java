package dummy.concept;

import java.util.logging.Level;
import java.util.logging.Logger;

import framework.concept.Task;

public class MobilityTask extends Task {

	private static Logger LOGGER = Logger.getLogger(MobilityTask.class.getName());
	private double timeLimit;
	private double distance;
	private double timeStart;
	private double purpose;

	public MobilityTask(double executingTime, double timeStart, double distance, double purpose, double timeLimit) {
		super(executingTime);
		this.timeStart = timeStart;
		this.purpose = purpose;
		this.timeLimit = timeLimit;
		this.distance = distance;

		LOGGER.log(Level.FINER, "New event/task is initialised: " + toString());

	}

	public double getTimeLimit() {
		return this.timeLimit;
	}

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
		String result = "TransportTask, starting time: " + this.executingTime;
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
