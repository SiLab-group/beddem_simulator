package beddem_simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import dummy.simulator.ContextManager;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.DefaultParameters;

public class MyTest {

	@BeforeClass
	public static void setUp() throws Exception {
		Schedule schedule = new Schedule();

		DefaultParameters parm = new DefaultParameters();
		parm.addParameter("checkpoints_in_simulate", "Checkpoints in Simulate", Integer.class, 1, true);
		parm.addParameter("periods_to_checkpoint", "Periods to checkpoint", Integer.class, 1, true);
		parm.addParameter("agent_made_probabilistic_decision", "Agent made probabilistic decision", Integer.class, 0,
				true);

		RunEnvironment.init(schedule, null, parm, false);
		Context<Object> context = new DefaultContext<Object>();
		RunState.init().setMasterContext(context);
	}

	@Test
	public void createContextBuilder() {
		ContextManager builder = new ContextManager();
		assertNotNull(builder);
	}

	@Test
	public void buildContextBuilder() {
		ContextManager builder = new ContextManager();

		Context<Object> returned_context = builder.build(new DefaultContext<Object>());
		// Return 3 contexts AgentContext, LocationContext, MainContext
		assertEquals("Number of contexts", 3, returned_context.size());
		assertEquals("Number of actionCounts", 3, RunEnvironment.getInstance().getCurrentSchedule().getActionCount());
	}

}
