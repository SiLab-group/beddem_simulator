package example;

import framework.concept.Option;

/**
 * A single mobility alternative for the Sion -> Sierre worked example
 * (BedDeM paper, Table 1). It carries the raw determinant inputs for one
 * transport mode.
 *
 * <p>All values follow the engine's cost convention: <b>lower = more
 * preferred</b>. Ranks (norm/role/self/comfort) use 1 = best .. 3 = worst;
 * price is in CHF, time in hours, frequency and facilitating are the raw
 * Table 1 values. This keeps the whole example internally consistent with
 * beddem_simulator's selection rule, which picks the option with the lowest
 * aggregated expected utility.</p>
 */
public class SionMobilityOption implements Option {

	private final String service;
	private final double price;         // CHF, lower = cheaper = better
	private final double time;          // hours, lower = faster = better
	private final double norm;          // social-norm rank, 1 = best
	private final double role;          // environmental rank, 1 = greenest = best
	private final double selfConcept;   // identity rank, 1 = best
	private final double comfort;       // (dis)comfort rank, 1 = best
	private final double freq;          // habit, lower = more habitual = better
	private final double facilitating;  // ease of access, lower = easier = better

	public SionMobilityOption(String service, double price, double time, double norm, double role, double selfConcept,
			double comfort, double freq, double facilitating) {
		this.service = service;
		this.price = price;
		this.time = time;
		this.norm = norm;
		this.role = role;
		this.selfConcept = selfConcept;
		this.comfort = comfort;
		this.freq = freq;
		this.facilitating = facilitating;
	}

	public String getService()      { return this.service; }
	public double getPrice()        { return this.price; }
	public double getTime()         { return this.time; }
	public double getNorm()         { return this.norm; }
	public double getRole()         { return this.role; }
	public double getSelfConcept()  { return this.selfConcept; }
	public double getComfort()      { return this.comfort; }
	public double getFreq()         { return this.freq; }
	public double getFacilitating() { return this.facilitating; }

	@Override
	public String toString() {
		return this.service;
	}
}
