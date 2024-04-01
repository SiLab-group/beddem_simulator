package dummy.agent;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import dummy.simulator.GlobalVars;
import framework.agent.core.CommunicationComponent;
import framework.concept.Feedback;
import framework.concept.InternalState;
import framework.concept.Option;
import framework.concept.Task;
import framework.environment.Environment;

public class DummyCommunicationComponent implements CommunicationComponent {

	private static Logger LOGGER = Logger.getLogger(DummyCommunicationComponent.class.getName());

	@Override
	public Option pickOption(Map<Double, Set<Option>> evaluatedOptions) {
		Option pickedOpt;

		if (GlobalVars.SIMULATION_PARAMS.AGENT_PROBABILISTIC_DECISION == 0) {
			LOGGER.log(Level.FINER, "Pick the best option");
			pickedOpt = pickBestOpt(evaluatedOptions);
		} else {
			pickedOpt = pickWithProbability(evaluatedOptions);
			LOGGER.log(Level.FINER, "Pick option with probability");
		}
		return pickedOpt;
	}

	private Option pickBestOpt(Map<Double, Set<Option>> evaluatedOptions) {
		if (evaluatedOptions.size() != 0) {
			double bestVal = 1;
			for (Double val : evaluatedOptions.keySet()) {
				if (val < bestVal) {
					bestVal = val;
				}
			}
			Set<Option> selectedOpts = evaluatedOptions.get(bestVal);
			int item = new Random().nextInt(selectedOpts.size());
			int i = 0;
			for (Option opt : selectedOpts) {
				if (i == item) {
					return opt;
				}

				i++;
			}
		}
		return null;
	}

	private Option pickWithProbability(Map<Double, Set<Option>> evaluatedOptions) {
		// Select a random option from the first element of the list.
		double total = 0;
		Map<Double, Set<Option>> probabilityMap = new TreeMap<Double, Set<Option>>();
		for (Map.Entry<Double, Set<Option>> entry : evaluatedOptions.entrySet()) {
			total += entry.getKey();
		}
		double newTotal = 0;
		for (Map.Entry<Double, Set<Option>> entry : evaluatedOptions.entrySet()) {
			double proKey = 1 - (entry.getKey() / total);
			newTotal += proKey;
			probabilityMap.put(proKey, entry.getValue());
		}
		Random rand = new Random();
		double randomValue = newTotal * rand.nextDouble();
		double temp = 0;
		for (Map.Entry<Double, Set<Option>> entry : probabilityMap.entrySet()) {
			temp += entry.getKey();
			if (temp >= randomValue) {
				int item = new Random().nextInt(entry.getValue().size());
				int i = 0;
				for (Option opt : entry.getValue()) {
					if (i == item)
						return opt;
					i++;
				}
			}
		}
		return null;
	}

	@Override
	public Feedback getFeedback(Task task, Option pickedOption, InternalState internalState, Environment loc) {
		// TODO Auto-generated method stub
		return null;
	}

}
