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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import dummy.agent.StandardDummyAgent;
import dummy.concept.MobilityTask;
import dummy.concept.Vehicle;
import dummy.context.AgentContext;
import dummy.context.LocationContext;
import dummy.environment.Location;
import dummy.report.DummyReporter;
import dummy.report.IReporter;
import dummy.simulator.ContextManager;
import dummy.simulator.GlobalVars;
import framework.agent.core.IAgent;
import framework.environment.Environment;

/**
 * The class that has the functions to read csv files and create agents,
 * locations, tasks and assign them to their contexts.
 * 
 * @author khoa_nguyen
 *
 */
public class CSVReader {

	private Logger LOGGER = Logger.getLogger(CSVReader.class.getName());
	private Map<String, Vehicle> idToVehicleMap;
//  Are these maps needed?
//	private Map<String, IAgent> idToAgentMap;
//	private Map<String, Location> idToLocation;

	/**
	 * Read the file and return the list of all the row in the file.
	 * 
	 * @param fileLocation The location of the file.
	 * @return The list of lines/rows in the file.
	 * @throws IOException
	 */
	private BufferedReader getBufferReaderForFile(String fileLocation) throws IOException {
		File file = null;
		file = new File(fileLocation);
		if (!file.exists()) {
			LOGGER.log(Level.FATAL, "Could not find the given file: " + file.getAbsolutePath());
			throw new FileNotFoundException("Could not find the given file: " + file.getAbsolutePath());
		}
		return new BufferedReader(new FileReader(file));
	}

	public void createResources() throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String vehicleFile = csvDataDir + ContextManager.getProperty(GlobalVars.VehicleCSVfile) + ".csv";

		this.idToVehicleMap = new HashMap<String, Vehicle>();
		BufferedReader br = getBufferReaderForFile(vehicleFile);
		String line;
		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] inputs = line.split(",");
			Vehicle vehicle = new Vehicle(inputs[0], inputs[1], Double.parseDouble(inputs[2]),
					Double.parseDouble(inputs[3]));
			this.idToVehicleMap.put(inputs[0], vehicle);
		}
		br.close();
	}

	public void createLocations(LocationContext locContext, HashMap<String, Environment> idToLocationMap)
			throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String locationFile = csvDataDir + ContextManager.getProperty(GlobalVars.LocationCSVfile) + ".csv";

		BufferedReader br = getBufferReaderForFile(locationFile);
		String line;
		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] inputs = line.split(",");
			Set<Vehicle> publicTransports = new HashSet<Vehicle>();

			// Add train to location
			if (inputs[1].equals("1")) {
				publicTransports.add(this.idToVehicleMap.get("1"));
			}
			// Add bus to location
			if (inputs[2].equals("1")) {
				publicTransports.add(this.idToVehicleMap.get("2"));
			}
			// Add tram to location
			if (inputs[3].equals("1")) {
				publicTransports.add(this.idToVehicleMap.get("3"));
			}
			Location loc = new Location(inputs[0], publicTransports);
			idToLocationMap.put(inputs[0], loc);
			locContext.add(loc);
		}

		br.close();
		LOGGER.log(Level.DEBUG, "Locations created: " + idToLocationMap.toString());
	}

	public void createAgents(AgentContext agentContext, HashMap<String, Environment> idToLocationMap,
			HashMap<String, IAgent> idToAgentMap) throws IOException {

		String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
		String agentFile = csvDataDir + ContextManager.getProperty(GlobalVars.AgentCSVfile) + ".csv";

		// this.idToAgentMap = new HashMap<String, IAgent>();
		BufferedReader br = getBufferReaderForFile(agentFile);
		String line;
		br.readLine();
		while ((line = br.readLine()) != null) {
			LOGGER.log(Level.DEBUG, "Line" + line);
			String[] inputs = line.split(",");
			double initialFund = Double.parseDouble(inputs[2]);
			String[] vehicleIDs = inputs[3].split(";");
			Set<Vehicle> ownVehicles = new HashSet<Vehicle>();
			for (String vehicleID : vehicleIDs) {
				ownVehicles.add(this.idToVehicleMap.get(vehicleID));
			}

			double beliefWeight = 0;
			double evaluationWeight = 1;
			double timeWeight = Double.parseDouble(inputs[4]);
			double costWeight = Double.parseDouble(inputs[5]);
			double normWeight = Double.parseDouble(inputs[6]);
			double roleWeight = Double.parseDouble(inputs[7]);
			double selfWeight = Double.parseDouble(inputs[8]);
			double emotionWeight = Double.parseDouble(inputs[9]);
			double facilitatingWeight = Double.parseDouble(inputs[10]);
			double freqWeight = Double.parseDouble(inputs[11]);
			double attitudeWeight = Double.parseDouble(inputs[12]);
			double socialWeight = Double.parseDouble(inputs[13]);
			double affectWeight = Double.parseDouble(inputs[14]);
			double intentionWeight = Double.parseDouble(inputs[15]);
			double habitWeight = Double.parseDouble(inputs[16]);

			StandardDummyAgent agent = new StandardDummyAgent(inputs[0], idToLocationMap.get(inputs[1]), initialFund,
					ownVehicles, beliefWeight, evaluationWeight, timeWeight, costWeight, normWeight, roleWeight,
					selfWeight, emotionWeight, facilitatingWeight, freqWeight, attitudeWeight, socialWeight,
					affectWeight, intentionWeight, habitWeight);
			LOGGER.log(Level.DEBUG, "Agent id " + inputs[0] + " agent " + agent.toString());
			idToAgentMap.put(inputs[0], agent);
			agentContext.add(agent);
		}
		br.close();

	}

	public void createSchedule(Map<String, IAgent> idToAgentMap, int periodNum, int checkpointNum) throws IOException {
		try {
			String csvDataDir = ContextManager.getProperty(GlobalVars.CSVDataDirectory);
			String scheduleFile = csvDataDir + ContextManager.getProperty(GlobalVars.ScheduleCSVfile) + "." + periodNum
					+ ".csv";

			BufferedReader br = getBufferReaderForFile(scheduleFile);
			String line;
			br.readLine();

			while ((line = br.readLine()) != null) {
				String[] inputs = line.split(",");
				String agentId = inputs[0];
				double time = Double.parseDouble(inputs[1]);
				double distance = Double.parseDouble(inputs[2]);
				double timeLimit = Double.parseDouble(inputs[3]);
				double purpose = Double.parseDouble(inputs[4]);

				time += GlobalVars.SIMULATION_PARAMS.TIME_STEPS_IN_PERIOD * periodNum
						+ GlobalVars.SIMULATION_PARAMS.TIME_STEPS_IN_PERIOD
								* GlobalVars.SIMULATION_PARAMS.getPeriodToNextCheckNum() * checkpointNum;
				LOGGER.debug("Create schedule time:  " + time);
				MobilityTask task = new MobilityTask(time, Double.parseDouble(inputs[1]), distance, purpose, timeLimit);
				StandardDummyAgent agent = (StandardDummyAgent) idToAgentMap.get(agentId);
				agent.addToSchedule(task);
				// agent.rememberLastTask(task);
				ContextManager.scheduleNewTask(agent, time);
				LOGGER.log(Level.DEBUG, "Scheduled demand at: " + time + "for agent: " + agent.getID());
			}

			br.close();
		} catch (IOException ex) {
			LOGGER.log(Level.FATAL, ex.getMessage(), ex);
			throw ex;
		}
	}

	public IReporter createDummyReporter(AgentContext agentContext) {
		return new DummyReporter(agentContext);
	}

}
