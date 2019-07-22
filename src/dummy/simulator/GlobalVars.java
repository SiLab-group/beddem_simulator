package dummy.simulator;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

/**
 * Placeholder of all constants for the simulation.
 * 
 * @author khoa_nguyen
 *
 */
public abstract class GlobalVars {

	// These are strings that match entries in the
	// beddem_simulator.properties file.
	public static final String CSVDataDirectory = "CSVDataDirectory";
	public static final String ResultDirectory = "ResultDirectory";
	public static final String AgentCSVfile = "AgentCSVfile";
	public static final String LocationCSVfile = "LocationCSVfile";
	public static final String ScheduleCSVfile = "ScheduleCSVfile";
	public static final String VehicleCSVfile = "VehicleCSVfile";
	public static final String ResourceCSVfile = "ResourceCSVfile";
	public static final String CorrespondenceCSVfile = "CorrespondenceCSVfile";
	public static final String EconomyCSVfile = "EconomyCSVfile";
	public static final String PolicyCSVfile = "PolicyCSVfile";
	public static final String RankingMatrixCSVfile = "RankingMatrixCSVfile";
	public static final String AgentWeightCSVfile = "AgentWeightCSVfile";
	public static final String AgentSociogramCSVfile = "AgentSociogramCSVfile";

	public static final String AgentTable = "AgentTable";
	public static final String CorrespondenceTable = "CorrespondenceTable";
	public static final String EconomyTable = "EconomyTable";
	public static final String ResourcesTable = "ResourcesTable";
	public static final String ScheduleTable = "ScheduleTable";
	public static final String WeightsTable = "WeightsTable";
	public static final String TransportationTable = "TransportationTable";
	public static final String TypeofvehiclesTable = "TypeofvehiclesTable";
	public static final String Sociogram = "Sociogram";

	public static final String postgreURL = "postgreURL";
	public static final String postgreUser = "postgreUser";
	public static final String postgrePassword = "postgrePassword";

	// Names of contexts and projections. These names must match those in the
	// parameters.xml file so that they can be displayed properly in the GUI.
	public static final class CONTEXT_NAMES {

		public static final String MAIN_CONTEXT = "maincontext";
		public static final String MAIN_GEOGRAPHY = "MainGeography";

		public static final String LOCATION_CONTEXT = "LocationContext";
		public static final String LOCATION_GEOGRAPHY = "LocationGeography";

		public static final String AGENT_CONTEXT = "AgentContext";
		public static final String AGENT_GEOGRAPHY = "AgentGeography";

	}

	/**
	 * Parameters that control the simulation time and step
	 * 
	 * @author khoa_nguyen
	 *
	 */
	public static final class SIMULATION_PARAMS {
		private static Parameters params = RunEnvironment.getInstance().getParameters();
		public static final int START_YEAR = (Integer) params.getValue("start_year");
		public static final int CHECKPOINTS_IN_SIMULATE = (Integer) params.getValue("checkpoints_in_simulate");
		public static final int PERIODS_TO_NEXT_CHECKPOINT = (Integer) params.getValue("periods_to_checkpoint");
		public static final int AGENT_PROBABILISTIC_DECISION = (Integer) params
				.getValue("agent_made_probabilistic_decision");
		public static final int POLICY_MATRIX_NUM = (Integer) params.getValue("policy_matrix_num");
		public static final int SELF_RANKING_NUM_1 = (Integer) params.getValue("self_ranking_1");
		public static final int SELF_RANKING_NUM_2 = (Integer) params.getValue("self_ranking_2");
		public static final int SELF_RANKING_NUM_3 = (Integer) params.getValue("self_ranking_3");
		public static final int SELF_RANKING_NUM_4 = (Integer) params.getValue("self_ranking_4");
		public static final int SCENARIO_VERSION = (Integer) params.getValue("scenario_version");
		public static final int NUMBER_OF_POLICIES = 4;
		public static final double TIME_STEPS_IN_PERIOD = 24 * 7 * 4 * 3;
		public static final double TIME_OF_EACH_SCHEDULE = 13;
		public static final int NUMBER_OF_MODES = 5;
		public static final double MAX_WEIGHT = 5.0;
	}

	public static final class AGENT_DECISION_PARAMS {
		// public static final double WEIGHT_OF_HABITS = 0.5;
		// public static final double WEIGHT_OF_INTENTION = 0.5;
		// public static final int OPTIONS_CONSIDERED = 10;

		public static final double ENVIRONMENTAL_RANKING_VALUE_OF_MOTOR_NW = 1.0;
		public static final double ENVIRONMENTAL_RANKING_VALUE_OF_MOTOR_E = 2.5;
		public static final double ENVIRONMENTAL_RANKING_VALUE_OF_MOTOR_G = 5.0;

		public static final double WORK_SPACE_RANKING_VALUE_OF_RAIL_BUS_TRAM = 1.0;
		public static final double WORK_SPACE_RANKING_VALUE_OF_OTHER_MODE = 5.0;

	}

	public static final class CALIBRATION_PARAMS {
		public static final double MAX_WEIGHT = 5;
		public static final double CENSUS_WALKING_KMS = 5802075451.0;// 2672484909.0; // 5802075451.0;
		public static final double CENSUS_BIKING_KMS = 2748351529.0;// 4907267234.0;// 2748351529.0;
		public static final double CENSUS_CAR_KMS = 72678629333.0;// 73092978474.0; // 72678629333.0;
		public static final double CENSUS_BUS_TRAM_KMS = 4580585882.0;// 4069602738.0; // 4580585882.0;
		public static final double CENSUS_TRAIN_KMS = 22902929412.0; // 23198972550.0; // 22902929412.0;
		public static final double CENSUS_OTHER_KMS = 3664468706.0;// 4422349369.0; // 3664468706.0;
		public static final double CENSUS_TOTAL_KMS = 112377040313.0;
	}

	public final static class GLOBAL_FUEL_PRICE {
		private static List<List<Double>> yearToPriceList = new ArrayList<List<Double>>();

		public static void addNewPrices(double priceOfG, double priceOfD, double priceOfE, double priceOfH) {
			List<Double> prices = new ArrayList<Double>();
			prices.add(priceOfG);
			prices.add(priceOfD);
			prices.add(priceOfE);
			prices.add(priceOfH);
			yearToPriceList.add(prices);
		}

		public static double getPrice(int checkpoint, String motorType) {
			List<Double> priceList = yearToPriceList.get(checkpoint);
			if (priceList == null) {
				return -1;
			} else {
				if (motorType.contains("G")) {
					return priceList.get(0);
				}
				if (motorType.contains("D")) {
					return priceList.get(1);
				}
				if (motorType.contains("E")) {
					return priceList.get(2);
				}
				if (motorType.contains("H")) {
					return priceList.get(3);
				}
				return 0;
			}

		}
	}

}
