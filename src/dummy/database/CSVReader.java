package dummy.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import dummy.context.AgentContext;
import dummy.context.LocationContext;
import dummy.report.DummyReporter;
import dummy.report.IReporter;
import dummy.simulator.ContextManager;
import dummy.simulator.GlobalVars;
import main.agent.core.IAgent;
import main.environment.Environment;

/**
 * The class that has the functions to read csv files and create agents,
 * locations, tasks and assign them to their contexts.
 * 
 * @author khoa_nguyen
 *
 */
public class CSVReader {

	private Logger LOGGER = Logger.getLogger(CSVReader.class.getName());
	private Set<String> vehicleCategories = new HashSet<String>();
	private Map<String, IAgent> idToAgentMap;
	private String[][] policyMatric;

	/**
	 * Read the file and return the list of all the row in the file.
	 * 
	 * @param fileLocation
	 *            The location of the file.
	 * @return The list of lines/rows in the file.
	 * @throws IOException
	 */
	private BufferedReader getBufferReaderForFile(String fileLocation) throws IOException {
		File file = null;
		file = new File(fileLocation);
		if (!file.exists()) {
			LOGGER.log(Level.SEVERE, "Could not find the given file: " + file.getAbsolutePath());
			throw new FileNotFoundException("Could not find the given file: " + file.getAbsolutePath());
		}
		return new BufferedReader(new FileReader(file));
	}

	/**
	 * Create locations from the read file and put them in the location context.
	 * 
	 * @param fileLocation
	 *            The location of the location file.
	 * @param context
	 *            The location context for the newly created locations.
	 * @param idToLocationMap
	 *            Map between the location and its id.
	 * @throws MissingElementException
	 * @throws IOException
	 */
	public void createLocations(LocationContext context, HashMap<String, Environment> idToLocationMap)
			throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String locationFile = csvDataDir + ContextManager.getProperty(GlobalVars.LocationCSVfile) + ".csv";

		BufferedReader br = getBufferReaderForFile(locationFile);
		String line;
		br.readLine();

		while ((line = br.readLine()) != null) {

		}

		br.close();
	}

	/**
	 * Create agents from the read file, get their location info from the location
	 * context and put them in the agent context.
	 * 
	 * @param fileLocation
	 *            The location of the agent file.
	 * @param agentContext
	 *            The agent context for newly created agents.
	 * @param idToLocationMap
	 *            Used to search for the specific location of the agent.
	 * @param idToAgentMap
	 *            Map between the agent and its id.
	 * @throws MissingElementException
	 * @throws IOException
	 */
	public void createAgents(AgentContext agentContext, HashMap<String, Environment> idToLocationMap,
			HashMap<String, IAgent> idToAgentMap) throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String agentFile = csvDataDir + ContextManager.getProperty(GlobalVars.AgentCSVfile) + ".csv";
		String agentWeightFile = csvDataDir + ContextManager.getProperty(GlobalVars.AgentWeightCSVfile) + ".csv";
		String rankingMatrixFile = csvDataDir + ContextManager.getProperty(GlobalVars.RankingMatrixCSVfile) + ".csv";
		String sociogramFile = csvDataDir + ContextManager.getProperty(GlobalVars.AgentSociogramCSVfile) + ".csv";

		BufferedReader br = getBufferReaderForFile(agentFile);
		String line;
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] inputs = line.split(",");

			Environment loc = idToLocationMap.get(inputs[6] + inputs[7]);

			Set<String> negModes = new HashSet<String>();
			Set<String> posModes = new HashSet<String>();

		}
		br.close();
	}

	/**
	 * Create schedule of tasks for the agent from the read file and assign them to
	 * the specific agent.
	 * 
	 * @param fileLocation
	 *            The location of the schedule file.
	 * @param checkpointNum
	 * @param periodNum
	 * @param context
	 *            The location context for the agent that has the task.
	 * @throws MissingElementException
	 * @throws IOException
	 */
	public void createSchedule(Map<String, IAgent> idToAgentMap, int periodNum, int checkpointNum) throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String scheduleFile = csvDataDir + ContextManager.getProperty(GlobalVars.ScheduleCSVfile) + "." + periodNum
				+ ".csv";

		BufferedReader br = getBufferReaderForFile(scheduleFile);
		String line;
		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] inputs = line.split(",");
		}

		br.close();
	}

	public void createResources(Map<String, IAgent> idToAgentMap) throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String vehicleFile = csvDataDir + ContextManager.getProperty(GlobalVars.VehicleCSVfile) + ".csv";
		String resourceFile = csvDataDir + ContextManager.getProperty(GlobalVars.ResourceCSVfile) + ".csv";
		String economyFile = csvDataDir + ContextManager.getProperty(GlobalVars.EconomyCSVfile) + ".csv";
		String policyFile = csvDataDir + ContextManager.getProperty(GlobalVars.PolicyCSVfile)
				+ GlobalVars.SIMULATION_PARAMS.POLICY_MATRIX_NUM + ".csv";

		updateAgentResource();
	}

	/**
	 * Create a reporter depend on which context to report.
	 * 
	 * @param agentContext
	 *            The agent context that can be observed and reported.
	 * @param locationContext
	 *            The location context that can be observed and reported.
	 * @return
	 */
	public IReporter createMobilityReporter(AgentContext agentContext, LocationContext locationContext) {
		return new DummyReporter(locationContext, agentContext, vehicleCategories);
	}

	public void updateAgentResource() {
		int year = GlobalVars.SIMULATION_PARAMS.START_YEAR + ContextManager.getCheckPointNum();
		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String correspondenceFile = csvDataDir + ContextManager.getProperty(GlobalVars.CorrespondenceCSVfile) + "."
				+ year + ".csv";

	}

}
