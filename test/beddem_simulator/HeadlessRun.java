package beddem_simulator;

import dummy.report.DummyReporter;
import dummy.simulator.ContextManager;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.DefaultParameters;

/**
 * Headless entry point for running the dummy (CSV-based) BedDeM model without the
 * Repast GUI or the batch/SessionsDriver machinery.
 *
 * It sets up a minimal Repast RunEnvironment exactly like ContextTest, builds the
 * context, runs the schedule to completion, and then invokes the reporter, which
 * writes output/decisions.csv. This is what CI runs to produce the decision output.
 */
public class HeadlessRun {

	public static void main(String[] args) {
		Schedule schedule = new Schedule();

		DefaultParameters parm = new DefaultParameters();
		// Number of days simulated. Each checkpoint replays the daily schedule
		// (data/schedule.0.csv) offset by +24h, so trips per agent = daily trips x
		// this value. 1 = a single day; raise it to show multi-day habit dynamics.
		parm.addParameter("checkpoints_in_simulate", "Checkpoints in Simulate", Integer.class, 1, true);
		parm.addParameter("periods_to_checkpoint", "Periods to checkpoint", Integer.class, 1, true);
		parm.addParameter("agent_made_probabilistic_decision", "Agent made probabilistic decision", Integer.class, 0,
				true);
		parm.addParameter("properties_file_name", "Properties file name", String.class,
				"beddem_simpleScenario.properties", true);

		RunEnvironment.init(schedule, null, parm, false);
		RunState.init().setMasterContext(new DefaultContext<Object>());

		ContextManager builder = new ContextManager();
		Context<Object> context = builder.build(new DefaultContext<Object>());

		// Run the schedule to completion. updateSchedule re-schedules itself each
		// period until the checkpoints are exhausted, so the schedule drains on its
		// own; the guard is just a safety stop against an unexpected infinite loop.
		ISchedule sched = RunEnvironment.getInstance().getCurrentSchedule();
		int guard = 0;
		while (sched.getActionCount() > 0 && guard++ < 1_000_000) {
			sched.execute();
		}
		System.out.println("Schedule finished after " + sched.getTickCount() + " ticks.");

		// Trigger the reporter -> writes output/decisions.csv (and prints the report).
		for (Object o : context.getObjects(DummyReporter.class)) {
			System.out.println(((DummyReporter) o).printReport());
		}
		System.out.println("Headless run complete.");
	}
}
