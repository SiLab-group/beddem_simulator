package dummy.agent;

import java.util.Map;
import java.util.Set;

import dummy.concept.MobilityOption;
import main.agent.core.CommunicationComponent;
import main.agent.core.DecisionComponent;
import main.agent.core.MemoryComponent;
import main.agent.core.PerceptionComponent;
import main.agent.core.TaskExecutionAgent;
import main.agent.reasoning.Determinant;
import main.agent.reasoning.LeafDeterminant;
import main.agent.reasoning.ParentDeterminant;
import main.concept.Feedback;
import main.concept.InternalState;
import main.concept.Option;
import main.concept.Task;
import main.environment.Environment;

/**
 * A class represent a standard agent that has mobility demand.
 * 
 * @author khoa_nguyen
 *
 */
public class StandardDummyAgent extends TaskExecutionAgent {

	private double beliefWeight;
	// private double evaluationWeight;
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

	public StandardDummyAgent(String id, Environment loc, double beliefWeight, double timeWeight, double costWeight,
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
				return mobilityOpt.getTime();
			}
		});
		evaluation.addDeterminantChild(new LeafDeterminant("cost",this.costWeight) {
			
			@Override
			protected double evalOpt(Option opt, Task task) {
				MobilityOption mobilityOption = (MobilityOption) opt;
				return mobilityOption.getCost();
			}
		});
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

	@Override
	protected PerceptionComponent createPerceptionComponent() {
		return new DummyPerceptionComponent();
	}

	@Override
	protected MemoryComponent createMemoryComponent() {
		return new DummyMemoryComponent();
	}

	@Override
	protected CommunicationComponent createCommunicationComponent() {
		return new DummyCommunicationComponent();
	}

}
