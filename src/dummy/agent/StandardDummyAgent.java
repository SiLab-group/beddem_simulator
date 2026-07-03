package dummy.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import dummy.concept.MobilityOption;
import dummy.concept.Vehicle;
import framework.agent.core.CommunicationComponent;
import framework.agent.core.DecisionComponent;
import framework.agent.core.MemoryComponent;
import framework.agent.core.PerceptionComponent;
import framework.agent.core.TaskExecutionAgent;
import framework.agent.reasoning.Determinant;
import framework.agent.reasoning.LeafDeterminant;
import framework.agent.reasoning.ParentDeterminant;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

/**
 * A class represent a standard agent that has mobility demand.
 *
 * @author khoa_nguyen
 *
 */
public class StandardDummyAgent extends TaskExecutionAgent {

	private static Logger LOGGER = Logger.getLogger(StandardDummyAgent.class.getName());

	private double beliefWeight;
	private double evaluationWeight;
	private double timeWeight;
	private double costWeight;
	private double normWeight;
	private double roleWeight;
	private double selfWeight;
	private double emotionWeight;
	private double facilitatingWeight;
	private double freqWeight;
	private double attitudeWeight;
	private double socialWeight;
	private double affectWeight;
	private double intentionWeight;
	private double habitWeight;

	private double initialFund;
	private Set<Vehicle> ownVehicles;

	public StandardDummyAgent(String id, Environment loc, double initialFund, Set<Vehicle> ownVehicles,
			double beliefWeight, double evaluationWeight, double timeWeight, double costWeight, double normWeight,
			double roleWeight, double selfWeight, double emotionWeight, double facilitatingWeight, double freqWeight,
			double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(id, loc);
		this.beliefWeight = beliefWeight;
		this.evaluationWeight = evaluationWeight;
		this.timeWeight = timeWeight;
		this.costWeight = costWeight;
		this.normWeight = normWeight;
		this.roleWeight = roleWeight;
		this.selfWeight = selfWeight;
		this.emotionWeight = emotionWeight;
		this.facilitatingWeight = facilitatingWeight;
		this.freqWeight = freqWeight;
		this.attitudeWeight = attitudeWeight;
		this.socialWeight = socialWeight;
		this.affectWeight = affectWeight;
		this.intentionWeight = intentionWeight;
		this.habitWeight = habitWeight;

		this.initialFund = initialFund;
		this.ownVehicles = ownVehicles;

		LOGGER.log(Level.DEBUG, "Agent constructor for agent " + this.getID() + " with " + this.initialFund
				+ " and owned vehicles " + this.ownVehicles);
		// Overriding perception/memory/communication methods after the agent is
		// constructed
		this.setupOverrides();
	}

	@Override
	protected PerceptionComponent createPerceptionComponent() {
		LOGGER.log(Level.DEBUG, "Create perception component agent id " + this.getID());
		return new DummyPerceptionComponent(this.getID());
	}

	@Override
	protected MemoryComponent createMemoryComponent() {
		LOGGER.log(Level.DEBUG, "Memory component " + this.getID() + " fund: " + this.initialFund + " owned vehicles "
				+ this.ownVehicles);
		return new DummyMemoryComponent(this.getID(), this.initialFund, this.ownVehicles);
	}

	@Override
	protected CommunicationComponent createCommunicationComponent() {
		LOGGER.log(Level.DEBUG, "Create communication component");
		return new DummyCommunicationComponent();
	}

	@Override
	protected DecisionComponent createDecisionComponent() {
		Determinant belief = createBeliefDeterminant();
		Determinant evaluation = createEvaluationDeterminant();
		Determinant norm = createNormDeterminant();
		Determinant role = createRoleDeterminant();
		Determinant selfConcept = createSelfDeterminant();
		Determinant emotion = createEmotionDeterminant();
		Determinant facilitatingCond = createFacilitatingDeterminant();
		Determinant freq = createFreqDeterminant();
		return new DummyDecisionComponent(belief, evaluation, norm, role, selfConcept, emotion, facilitatingCond, freq,
				this.attitudeWeight, this.socialWeight, this.affectWeight, this.intentionWeight, this.habitWeight);
	}

	/*******************************************************************************************/
	private Determinant createBeliefDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createEvaluationDeterminant() {
		ParentDeterminant evaluation = new ParentDeterminant("evaluation", this.evaluationWeight);
		evaluation.addDeterminantChild(new LeafDeterminant("time", this.timeWeight) {

			@Override
			protected double evalOpt(Option opt, Task task) {
				MobilityOption mobilityOpt = (MobilityOption) opt;
				LOGGER.log(Level.DEBUG, "Evaluating TIME option " + mobilityOpt.getTime());
				return mobilityOpt.getTime();
			}
		});
		evaluation.addDeterminantChild(new LeafDeterminant("cost", this.costWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				MobilityOption mobilityOption = (MobilityOption) opt;
				LOGGER.log(Level.DEBUG, "Evaluating COST option " + mobilityOption.getCost());
				return mobilityOption.getCost();
			}
		});
		LOGGER.log(Level.DEBUG, " Evalutation " + evaluation.toString());
		return evaluation;
	}

	private Determinant createNormDeterminant() {
		// Social norm: how socially accepted the mode is (car slightly less so).
		return new LeafDeterminant("norm", this.normWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				return logged("NORM", opt, lookup(SOCIAL_NORM, modeOf(opt)));
			}
		};
	}

	private Determinant createRoleDeterminant() {
		// Role / environmental values: emissions penalty (active < public < car).
		return new LeafDeterminant("role", this.roleWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				return logged("ROLE", opt, lookup(EMISSIONS, modeOf(opt)));
			}
		};
	}

	private Determinant createSelfDeterminant() {
		// Self-concept: the agent identifies with the modes it owns.
		return new LeafDeterminant("self", this.selfWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				Vehicle v = ((MobilityOption) opt).getMainVehicle();
				return logged("SELF", opt, StandardDummyAgent.this.ownVehicles.contains(v) ? 0.0 : 2.0);
			}
		};
	}

	private Determinant createEmotionDeterminant() {
		// Emotion / comfort: perceived (dis)comfort of the mode (car comfiest).
		return new LeafDeterminant("emotion", this.emotionWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				return logged("EMOTION", opt, lookup(DISCOMFORT, modeOf(opt)));
			}
		};
	}

	private Determinant createFacilitatingDeterminant() {
		// Facilitating conditions: convenience / directness (transfers penalised).
		return new LeafDeterminant("facilitating", this.facilitatingWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				return logged("FACILITATING", opt, lookup(INCONVENIENCE, modeOf(opt)));
			}
		};
	}

	private Determinant createFreqDeterminant() {
		// Habit: modes used more often in the past get a lower penalty. Reads the
		// running frequency count from the agent's memory, so this determinant
		// reflects the agent's earlier choices during the simulation.
		return new LeafDeterminant("freq", this.freqWeight) {
			@Override
			protected double evalOpt(Option opt, Task task) {
				Vehicle v = ((MobilityOption) opt).getMainVehicle();
				int past = ((DummyMemoryComponent) StandardDummyAgent.this.memoryComponent).getPastFrequency(v);
				return logged("FREQ", opt, 1.0 / (1.0 + past));
			}
		};
	}

	/*
	 * Helper penalty tables. Every determinant above returns a cost-like value in
	 * which LOWER == more preferred, matching the engine's selection rule
	 * (DummyCommunicationComponent picks the option with the lowest EU). The six
	 * columns are the modes defined in data/vehicle.csv.
	 */
	private static String modeOf(Option opt) {
		return ((MobilityOption) opt).getMainVehicle().getName().toLowerCase();
	}

	/** Log a determinant's value for one option, then return it unchanged. */
	private static double logged(String determinant, Option opt, double value) {
		LOGGER.log(Level.DEBUG, "Evaluating " + determinant + " " + modeOf(opt) + " = " + value);
		return value;
	}

	private static double lookup(Map<String, Double> table, String mode) {
		// Unknown modes get a neutral mid penalty rather than crashing.
		return table.getOrDefault(mode, 2.0);
	}

	//                                             train  bus  tram  car  walking  biking
	private static final Map<String, Double> SOCIAL_NORM   = penalties(1, 1, 1, 2, 1, 1);
	private static final Map<String, Double> EMISSIONS     = penalties(1, 2, 1, 3, 0, 0);
	private static final Map<String, Double> DISCOMFORT    = penalties(1, 2, 1, 0, 3, 2);
	private static final Map<String, Double> INCONVENIENCE = penalties(1, 2, 1, 0, 0, 0);

	private static Map<String, Double> penalties(double train, double bus, double tram, double car, double walking,
			double biking) {
		Map<String, Double> t = new HashMap<String, Double>();
		t.put("train", train);
		t.put("bus", bus);
		t.put("tram", tram);
		t.put("car", car);
		t.put("walking", walking);
		t.put("biking", biking);
		return t;
	}
	/*******************************************************************************************/

}
