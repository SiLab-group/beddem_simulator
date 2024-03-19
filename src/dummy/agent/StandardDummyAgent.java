package dummy.agent;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	public StandardDummyAgent(String id, Environment loc, double initialFund, Set<Vehicle> ownVehicles, double beliefWeight, double timeWeight, double costWeight,
			double normWeight, double roleWeight, double selfWeight, double emotionWeight, double facilitatingWeight,
			double freqWeight, double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
			double habitWeight) {
		super(id, loc);
		this.beliefWeight = beliefWeight;
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
		
		LOGGER.log(Level.INFO,"Agent constructor " + this.getID() + " " + this.initialFund + " ");
		
		this.setup_overrides();
	}
	
	@Override
	protected PerceptionComponent createPerceptionComponent() {
		return new DummyPerceptionComponent(this.getID());
	}

	@Override
	protected MemoryComponent createMemoryComponent() {
		LOGGER.log(Level.INFO,"Memory component " + this.getID() + " " + this.initialFund + " ");
		return new DummyMemoryComponent(this.getID(), this.initialFund, this.ownVehicles);
	}

	@Override
	protected CommunicationComponent createCommunicationComponent() {
		return new DummyCommunicationComponent();
	}

	@Override
	protected DecisionComponent createDecisionComponent() {
		Determinant belief = createBeliefDeterminant();
		Determinant evaluation = createEvaluationDeterminant();
		Determinant norm = createNormDeterminant();
		Determinant role = createRoleDeterminant();
		Determinant self_concept = createSelfDeterminant();
		Determinant emotion = createEmotionDeterminant();
		Determinant facilitatingCond = createFacilitatingDeterminant();
		Determinant freq = createFreqDeterminant();
		return new DummyDecisionComponent(belief, evaluation, norm, role, self_concept, emotion, facilitatingCond, freq,
				 normWeight, roleWeight, selfWeight, emotionWeight, facilitatingWeight);
	}

	
	/*******************************************************************************************/
	private Determinant createBeliefDeterminant() {
		// TODO Auto-generated method stub
		return null;
	}

	private Determinant createEvaluationDeterminant() {
		ParentDeterminant evaluation = new ParentDeterminant("evaluation",1);
		evaluation.addDeterminantChild(new LeafDeterminant("time",this.timeWeight) {

			@Override
			protected double evalOpt(Option opt, Task task) {
				MobilityOption mobilityOpt = (MobilityOption) opt;
				LOGGER.log(Level.INFO, "Eval TIME option " + mobilityOpt.getTime());
				return mobilityOpt.getTime();
			}
		});
		evaluation.addDeterminantChild(new LeafDeterminant("cost",this.costWeight) {
			
			@Override
			protected double evalOpt(Option opt, Task task) {
				MobilityOption mobilityOption = (MobilityOption) opt;
				LOGGER.log(Level.INFO, "Eval COST option " + mobilityOption.getCost());
				return mobilityOption.getCost();
			}
		});
		LOGGER.log(Level.INFO," Evalutation " + evaluation.toString());
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
