package dummy.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import dummy.simulator.GlobalVars;
import main.agent.core.CommunicationComponent;
import main.concept.Opinion;
import main.concept.Option;
import main.concept.Task;

public class DummyCommunicationComponent implements CommunicationComponent {

	private static Logger LOGGER = Logger.getLogger(DummyCommunicationComponent.class.getName());

	@Override
	public Option pickOption(Map<Double, Set<Option>> evaluatedOptions) {
		if (GlobalVars.SIMULATION_PARAMS.AGENT_PROBABILISTIC_DECISION == 0) {
			return pickBestOpt(evaluatedOptions);
		} else {
			return pickWithProbability(evaluatedOptions);
		}

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
	public Opinion getOpinion(Option option, Task task) {
		// TODO Auto-generated method stub
		return null;
	}

}
