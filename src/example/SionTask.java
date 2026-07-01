package example;

import framework.concept.Task;

/**
 * Minimal task for the Sion -> Sierre worked example. The determinant
 * functions in {@link SionTIBDecision} read all they need from the option
 * itself, so this task only needs to satisfy the framework's Task contract.
 */
public class SionTask extends Task {

	public SionTask() {
		super(0.0);
	}
}
