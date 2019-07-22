package main.concept;

/**
 * An task that inside an agent's schedule. Needed to be add to the schedule of
 * ContextManager to be executed.
 * 
 * @author khoa_nguyen
 *
 */
public abstract class Task implements Comparable<Task> {

	protected double executingTime;

	public Task(double executingTime) {
		this.executingTime = executingTime;
	}

	/**
	 * Get the time when the task needed to be performed
	 * 
	 * @return The executing time of the event
	 */
	public double getExecutingTime() {
		return this.executingTime;
	}

	@Override
	public int compareTo(Task otherTask) {
		return Double.compare(this.executingTime, otherTask.executingTime);
	}
}
