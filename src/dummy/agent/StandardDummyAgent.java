package dummy.agent;

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
		ParentDeterminant evaluation = new ParentDeterminant("evaluation", 1);
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
		return new LeafDeterminant("norm", this.normWeight) {

			@Override
			protected double evalOpt(Option opt, Task task) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	private Determinant createRoleDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createSelfDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createEmotionDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createFacilitatingDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createFreqDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}
	/*******************************************************************************************/

}
