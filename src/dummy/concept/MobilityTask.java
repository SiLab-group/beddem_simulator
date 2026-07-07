package dummy.concept;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import framework.concept.Task;

public class MobilityTask extends Task {

	private static Logger LOGGER = Logger.getLogger(MobilityTask.class.getName());
	private double timeLimit;
	private double distance;
	private double timeStart;
	private double purpose;
	private String fromLoc;
	private String toLoc;
	private Set<Vehicle> originTransports;

	public MobilityTask(double executingTime, double timeStart, String fromLoc, String toLoc,
			Set<Vehicle> originTransports, double distance, double purpose, double timeLimit) {
		super(executingTime);
		this.timeStart = timeStart;
		this.fromLoc = fromLoc;
		this.toLoc = toLoc;
		this.originTransports = (originTransports == null) ? new HashSet<Vehicle>() : originTransports;
		this.purpose = purpose;
		this.timeLimit = timeLimit;
		this.distance = distance;

		LOGGER.log(Level.DEBUG, "New event/task is initialised: " + toString());

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

	/** Origin location of the trip. */
	public String getFromLoc() {
		return this.fromLoc;
	}

	/** Destination location of the trip. */
	public String getToLoc() {
		return this.toLoc;
	}

	/** Public transport available at the trip's origin. */
	public Set<Vehicle> getOriginTransports() {
		return this.originTransports;
	}

	@Override
	public String toString() {
		return "TransportTask " + this.fromLoc + " -> " + this.toLoc + ", starting time: " + this.executingTime;
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
