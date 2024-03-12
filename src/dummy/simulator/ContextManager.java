package dummy.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import dummy.context.AgentContext;
import dummy.context.LocationContext;
import dummy.database.CSVReader;
import dummy.report.IReporter;
import framework.agent.core.IAgent;
import framework.environment.Environment;
import framework.exception.EnvironmentError;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.PriorityType;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * The entry point and simulator operation of Repast. It sets up, manages and
 * stops all elements of the simulation.
 * 
 * @author khoa_nguyen
 * @see ThreadedAgentScheduler
 * @see IAgent
 * @see CSVReader
 */
public class ContextManager implements ContextBuilder<Object> {

	// A logger for this class. Note that there is a static block that is used
	// to configure all logging for the model (at the bottom of this file).
	private static Logger LOGGER = Logger.getLogger(ContextManager.class.getName());

	// Optionally force agent threading off (good for debugging).
	// private static final boolean TURN_OFF_THREADING = false;

	private static Properties properties;

	// Pointers to contexts and projections (for convenience). Most of these can
	// be made public, but the agent ones can't be because multi-threaded agents
	// will simultaneously try to call 'move()' and interfere with each other.
	// So methods like 'moveAgent()' are provided by ContextManager.
	private static Context<Object> mainContext;

	private static LocationContext locationContext;
	private static HashMap<String, Environment> idToLocationMap;

	private static AgentContext agentContext;
	private static HashMap<String, IAgent> idToAgentMap;

	// CSVReader generator;
	CSVReader generator;

	private static int periodNum;
	private static int checkpointNum;

	@Override
	public Context<Object> build(Context<Object> con) {

		SimulationLogging.init();

		periodNum = 0;
		checkpointNum = 0;

		// Read in the model properties.
		try {
			readProperties();
		} catch (IOException ex) {
			throw new RuntimeException("Could not read model properties, reason: " + ex.toString(), ex);
		}

		generator = new CSVReader();

		// Keep a useful static link to the simulator context.
		mainContext = con;

		// This is the name of the 'root'context.
		mainContext.setId(GlobalVars.CONTEXT_NAMES.MAIN_CONTEXT);

		// Create the location context.
		locationContext = new LocationContext();
		idToLocationMap = new HashMap<String, Environment>();

		try {
			generator.createResources();
			generator.createLocations(locationContext, idToLocationMap);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException("Could not create resources or locations,reason:  " + e1.toString(),e1);
		}

		mainContext.addSubContext(locationContext);

		// Now create the agents (note that their step methods are scheduled
		// later).
		agentContext = new AgentContext();
		idToAgentMap = new HashMap<String, IAgent>();

		try {
			generator.createAgents(agentContext, idToLocationMap, idToAgentMap);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO exception.", e);
		}

		mainContext.addSubContext(agentContext);

		// Read the schedule file and schedule all the agents' events.
		updateSchedule();

		// Create reporter for the simulation.
		IReporter mobilityReporter = generator.createMobilityReporter(agentContext);
		mainContext.add(mobilityReporter);
		LOGGER.log(Level.INFO," Returning main context");
		return mainContext;
	}

	public static int getCheckPointNum() {
		return checkpointNum;
	}

	/**
	 * Check the schedule files and update the agents' events and scheduler. The
	 * checkpoint is when agent need to update its scheduling files (if necessary).
	 * The period is when agent need to read new file and update its timetable.
	 */
	public void updateSchedule() {
		// If the simulator still hasn't reached the maximum checkpoints.
		if (checkpointNum < GlobalVars.SIMULATION_PARAMS.CHECKPOINTS_IN_SIMULATE) {
			// If the number of periods has reached the next checkpoints.
			if (periodNum == GlobalVars.SIMULATION_PARAMS.PERIODS_TO_NEXT_CHECKPOINT) {
				periodNum = 0;
				checkpointNum++;
			} else {
				// Add tasks to the agent's schedule.

				try {
					generator.createSchedule(idToAgentMap, periodNum, checkpointNum);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				periodNum++;
			}
//			 for (IAgent agent : agentContext) {
//			 agent.updateInternalState();
//			 }

			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createOneTime(
					GlobalVars.SIMULATION_PARAMS.TIME_STEPS_IN_PERIOD * periodNum
							+ GlobalVars.SIMULATION_PARAMS.TIME_STEPS_IN_PERIOD
									* GlobalVars.SIMULATION_PARAMS.PERIODS_TO_NEXT_CHECKPOINT * checkpointNum,
					PriorityType.LAST);
			schedule.schedule(params, this, "updateSchedule");
		}
	}

	// For recording time per N iterations (used for debugging).
	private static long speedTimer = -1;

	/**
	 * Get the next task in agent's schedule and schedule it to be run.
	 * 
	 * @param agent
	 *            The agent that has the next task to be scheduled.
	 */
	public static void scheduleNewTask(IAgent agent, double timeToSchedule) {
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters agentStepParams = ScheduleParameters.createOneTime(timeToSchedule);
		schedule.schedule(agentStepParams, agent, "step");
	}

	/**
	 * Log out the current iteration of the simulation (1 iteration = 1 scheduled
	 * tick).
	 */
	public void printTicks() {
		LOGGER.info("Iterations: " + RunEnvironment.getInstance().getCurrentSchedule().getTickCount() + ". Speed: "
				+ ((double) (System.currentTimeMillis() - ContextManager.speedTimer) / 1000.0) + "sec/ticks.");
		ContextManager.speedTimer = System.currentTimeMillis();
	}

	/**
	 * Get the current tick of the simulation. At the moment, 1 tick = 1 hour.
	 * 
	 * @return The current tick of the simulation.
	 */
	public static double getTicks() {
		return RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
	}

	/**
	 * Get the value of a property in the properties file. If the input is empty or
	 * null or if there is no property with a matching name, throw a
	 * RuntimeException.
	 * 
	 * @param property
	 *            The property to look for.
	 * @return A value for the property with the given name.
	 */
	public static String getProperty(String property) {
		if (property == null || property.equals("")) {
			throw new RuntimeException("getProperty() error, input parameter (" + property + ") is "
					+ (property == null ? "null" : "empty"));
		} else {
			String val = ContextManager.properties.getProperty(property);
			if (val == null || val.equals("")) { // No value exists in the
													// properties file
				throw new RuntimeException("checkProperty() error, the required property (" + property + ") is "
						+ (property == null ? "null" : "empty"));
			}
			return val;
		}
	}

	/**
	 * Read the properties file and add properties. Will check if any properties
	 * have been included on the command line as well as in the properties file, in
	 * these cases the entries in the properties file are ignored in preference for
	 * those specified on the command line.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readProperties() throws FileNotFoundException, IOException {

		LOGGER.log(Level.FINE, new File(".").getAbsolutePath());

		File propFile = new File("./data/beddem_simulator.properties");
		if (!propFile.exists()) {
			throw new FileNotFoundException(
					"Could not find properties file in the default location: " + propFile.getAbsolutePath());
		}

		LOGGER.log(Level.FINE, "Initialising properties from file " + propFile.toString());

		ContextManager.properties = new Properties();

		FileInputStream in = new FileInputStream(propFile.getAbsolutePath());
		ContextManager.properties.load(in);
		in.close();

		// See if any properties are being overridden by command-line arguments
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			String k = (String) e.nextElement();
			String newVal = System.getProperty(k);
			if (newVal != null) {
				// The system property has the same name as the one from the
				// properties file, replace the one in the properties file.
				LOGGER.log(Level.INFO,
						"Found a system property '" + k + "->" + newVal + "' which matches a NeissModel property '" + k
								+ "->" + properties.getProperty(k) + "', replacing the non-system one.");
				properties.setProperty(k, newVal);
			}
		}
	}

	/**
	 * Count the number of items in the provided iterable
	 * 
	 * @param i
	 *            The iterable list
	 * @return The number of items in the provided iterable
	 */
	private static int sizeOfIterable(Iterable<?> i) {
		int size = 0;
		Iterator<?> it = i.iterator();
		while (it.hasNext()) {
			size++;
			it.next();
		}
		return size;
	}

	/**
	 * Checks that the given <code>Context</code>s have more than zero objects in
	 * them
	 * 
	 * @param contexts
	 * @throws EnvironmentError
	 */
	public void checkSize(Context<?>... contexts) throws EnvironmentError {
		for (Context<?> c : contexts) {
			int numObjs = sizeOfIterable(c.getObjects(Object.class));
			if (numObjs == 0) {
				throw new EnvironmentError("There are no objects in the context: " + c.getId().toString());
			}
		}
	}

	/**
	 * Stop the simulation
	 * 
	 * @param ex
	 *            Define the exception
	 * @param clazz
	 *            The class that call the simulation to stop
	 */
	public static void stopSim(Exception ex, Class<?> clazz) {
		ISchedule sched = RunEnvironment.getInstance().getCurrentSchedule();
		sched.setFinishing(true);
		sched.executeEndActions();
		LOGGER.log(Level.SEVERE, "ContextManager has been told to stop by " + clazz.getName(), ex);
	}

	/**
	 * Get all the agents in the agent context. This method is required -- rather
	 * than giving agents direct access to the agentGeography -- because when
	 * multiple threads are used they can interfere with each other and agents end
	 * up moving incorrectly.
	 * 
	 * @return An iterable over all agents, chosen in a random order. See the
	 *         <code>getRandomObjects</code> function in <code>DefaultContext</code>
	 * @see DefaultContext
	 */
	public static synchronized Iterable<IAgent> getAllAgents() {
		return ContextManager.agentContext.getObjects(IAgent.class);
	}

}
