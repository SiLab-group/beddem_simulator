package example;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import framework.concept.Option;

/**
 * Runnable worked example: an 18 km trip from Sion to Sierre with three modes
 * (car, train, bike), using the weights and determinant values of the BedDeM
 * paper, Table 1, evaluated through beddem_simulator's own TIB engine.
 *
 * <p>Every determinant uses the cost convention (lower = better) and the agent
 * prefers the option with the lowest aggregated expected utility (EU).</p>
 *
 * <p>Run: {@code java example.SionToSierreExample}</p>
 */
public class SionToSierreExample {

	/** Build the three Table 1 options. */
	public static Set<Option> scenarioOptions() {
		Set<Option> options = new HashSet<Option>();
		//                                 service  price time norm role self comfort freq facil
		options.add(new SionMobilityOption("Car",   4.0, 0.3,  2,   3,   1,   1,      0,   0));
		options.add(new SionMobilityOption("Train", 3.0, 0.2,  1,   2,   2,   2,      0,   1));
		options.add(new SionMobilityOption("Bike",  0.0, 1.0,  3,   1,   3,   3,      1,   0));
		return options;
	}

	/** Build the decision component with the Table 1 weights. */
	public static SionTIBDecision scenarioDecision() {
		return SionTIBDecision.build(
				2,  // price
				4,  // time
				3,  // norm
				2,  // role
				3,  // self-concept
				1,  // emotion / enjoyment
				2,  // facilitating
				3,  // freq / habit
				1,  // evaluation node
				4,  // attitude
				2,  // social
				2,  // affect
				4,  // intention
				3); // habit
	}

	/** Map each option's service name to its aggregated EU (lower = better). */
	public static Map<String, Double> euByService() {
		Map<Double, Set<Option>> result = scenarioDecision().evaluateOptions(scenarioOptions(), new SionTask());
		java.util.Map<String, Double> eu = new java.util.HashMap<String, Double>();
		for (Map.Entry<Double, Set<Option>> entry : result.entrySet()) {
			for (Option opt : entry.getValue()) {
				eu.put(((SionMobilityOption) opt).getService(), entry.getKey());
			}
		}
		return eu;
	}

	public static void main(String[] args) {
		Map<String, Double> eu = euByService();
		String winner = null;
		double best = Double.POSITIVE_INFINITY;
		System.out.println("=== Sion -> Sierre (18 km) | EU, lower = better ===");
		for (String mode : new String[] { "Car", "Train", "Bike" }) {
			double v = eu.get(mode);
			System.out.printf(java.util.Locale.US, "  %-6s EU = %.4f%n", mode, v);
			if (v < best) { best = v; winner = mode; }
		}
		System.out.println("Chosen (lowest EU): " + winner);
	}
}
