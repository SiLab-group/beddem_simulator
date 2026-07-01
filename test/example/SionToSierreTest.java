package example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Worked-example test: an 18 km trip from Sion to Sierre with three modes
 * (car, train, bike), using the weights and determinant values of the BedDeM
 * paper, Table 1, evaluated through beddem_simulator's own TIB engine
 * ({@link SionTIBDecision}).
 *
 * <p>The engine uses cost semantics: every determinant is expressed so that a
 * <b>lower value is more preferred</b>, and the agent chooses the option with
 * the <b>lowest</b> aggregated expected utility (EU). Under the Table 1 inputs
 * the ranking is Car &lt; Train &lt; Bike, so the agent picks the car.</p>
 */
public class SionToSierreTest {

	// Expected EU from the paper, Table 1 (lower = better).
	private static final double EXPECTED_CAR = 1.13;
	private static final double EXPECTED_TRAIN = 3.07;
	private static final double EXPECTED_BIKE = 4.81;
	private static final double TOLERANCE = 0.05;

	private Map<String, Double> eu;

	@Before
	public void setUp() {
		eu = SionToSierreExample.euByService();
	}

	@Test
	public void allThreeOptionsAreEvaluated() {
		assertEquals("Three modes should be evaluated", 3, eu.size());
	}

	@Test
	public void agentPicksLowestEuOption() {
		String winner = null;
		double best = Double.POSITIVE_INFINITY;
		for (Map.Entry<String, Double> e : eu.entrySet()) {
			if (e.getValue() < best) {
				best = e.getValue();
				winner = e.getKey();
			}
		}
		assertEquals("Car has the lowest aggregated EU and is chosen", "Car", winner);
	}

	@Test
	public void rankingIsCarThenTrainThenBike() {
		assertTrue("Car should be preferred over Train (lower EU)", eu.get("Car") < eu.get("Train"));
		assertTrue("Train should be preferred over Bike (lower EU)", eu.get("Train") < eu.get("Bike"));
	}

	@Test
	public void euMatchesPaperTable1() {
		assertEquals("Car EU", EXPECTED_CAR, eu.get("Car"), TOLERANCE);
		assertEquals("Train EU", EXPECTED_TRAIN, eu.get("Train"), TOLERANCE);
		assertEquals("Bike EU", EXPECTED_BIKE, eu.get("Bike"), TOLERANCE);
	}

	@Test
	public void allEuValuesArePositiveAndFinite() {
		for (double v : eu.values()) {
			assertTrue("EU should be finite", Double.isFinite(v));
			assertTrue("EU should be positive", v > 0);
		}
	}
}
