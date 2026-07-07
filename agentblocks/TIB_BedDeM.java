/**
 * TIB Agent Block: self-contained, illustrative reimplementation of the BedDeM
 * TIB decision block. It mirrors the determinant hierarchy and the minimum-
 * expected-utility choice rule of framework.agent.reasoning.TIBModel, and it
 * implements the RBB's expected-utility equation:
 *
 *     EU_d(option) = Σ_a ( EU_a(option) / Σ_o EU_a(o) ) · w_a
 *
 * i.e. every determinant's value is normalised across all options BEFORE it is
 * weighted into its parent. This normalisation is applied at every level, exactly
 * as the production engine does via Determinant.rankOptions().
 *
 * Reference: https://github.com/SiLab-group/beddem_simulator
 */
package agentblocks.tib;

import java.util.*;

interface Option {
    String getName();
    double getProperty(String propertyName);
}

class SimpleOption implements Option {
    private final String name;
    private final Map<String, Double> properties = new HashMap<>();
    public SimpleOption(String name) { this.name = name; }
    public String getName() { return name; }
    public double getProperty(String propertyName) {
        return properties.getOrDefault(propertyName, 0.0);
    }
    public void setProperty(String propertyName, double value) {
        properties.put(propertyName, value);
    }
}

interface Determinant {
    String getName();
    double getWeight();
    void setWeight(double weight);
    /**
     * Evaluate every option at this determinant and return its NORMALISED score
     * (each option's share of the total across all options). Normalising here, at
     * every level, is the Σ_o EU_a(o) term of the EU equation.
     */
    Map<Option, Double> evaluate(List<? extends Option> options);
}

abstract class AbstractDeterminant implements Determinant {
    protected final String name;
    protected double weight;
    protected AbstractDeterminant(String name, double weight) {
        this.name = name; this.weight = weight;
    }
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    /** Divide each option's raw value by the sum across all options. */
    protected static Map<Option, Double> normalise(Map<Option, Double> raw) {
        double sum = 0.0;
        for (double v : raw.values()) sum += v;
        Map<Option, Double> out = new HashMap<>();
        for (Map.Entry<Option, Double> e : raw.entrySet())
            out.put(e.getKey(), sum > 0 ? e.getValue() / sum : 0.0);
        return out;
    }
}

/** Base determinant: reads one property off each option. */
class LeafDeterminant extends AbstractDeterminant {
    public LeafDeterminant(String name, double weight) { super(name, weight); }
    @Override
    public Map<Option, Double> evaluate(List<? extends Option> options) {
        Map<Option, Double> raw = new HashMap<>();
        for (Option opt : options) raw.put(opt, opt.getProperty(name));
        return normalise(raw);
    }
}

/** Aggregates children: Σ_child ( normalised child score · child weight ), then normalises. */
class ParentDeterminant extends AbstractDeterminant {
    private final List<Determinant> children = new ArrayList<>();
    public ParentDeterminant(String name, double weight) { super(name, weight); }
    public void addDeterminantChild(Determinant child) { children.add(child); }
    public List<Determinant> getChildren() { return children; }
    @Override
    public Map<Option, Double> evaluate(List<? extends Option> options) {
        Map<Option, Double> agg = new HashMap<>();
        for (Option opt : options) agg.put(opt, 0.0);
        for (Determinant child : children) {
            Map<Option, Double> childScores = child.evaluate(options);
            for (Option opt : options)
                agg.put(opt, agg.get(opt) + child.getWeight() * childScores.get(opt));
        }
        return normalise(agg);
    }
}

/**
 * Triandis' TIB decision model. Provide the eight base determinants and the five
 * aggregate weights; the hierarchy below is wired exactly as in BedDeM. To model
 * additional psychology, subclass and add determinants to the tree.
 */
class TIBModel extends ParentDeterminant {
    public TIBModel(Determinant belief, Determinant evaluation, Determinant norm, Determinant role,
                    Determinant self_concept, Determinant emotion, Determinant facilitatingCond, Determinant freq,
                    double attitudeWeight, double socialWeight, double affectWeight, double intentionWeight,
                    double habitWeight) {
        super("Triandis", 1);
        ParentDeterminant intention = new ParentDeterminant("intention", intentionWeight);
        ParentDeterminant habits    = new ParentDeterminant("habits", habitWeight);
        ParentDeterminant attitude  = new ParentDeterminant("attitude", attitudeWeight);
        ParentDeterminant social    = new ParentDeterminant("social", socialWeight);
        ParentDeterminant affect    = new ParentDeterminant("affect", affectWeight);

        addDeterminantChild(intention);
        addDeterminantChild(habits);
        addDeterminantChild(facilitatingCond);

        habits.addDeterminantChild(freq);
        intention.addDeterminantChild(affect);
        intention.addDeterminantChild(social);
        intention.addDeterminantChild(attitude);
        affect.addDeterminantChild(emotion);
        social.addDeterminantChild(norm);
        social.addDeterminantChild(role);
        social.addDeterminantChild(self_concept);
        attitude.addDeterminantChild(belief);
        attitude.addDeterminantChild(evaluation);
    }

    /** Expected utility per option (normalised). */
    public Map<Option, Double> evaluateOptions(List<? extends Option> options) {
        return evaluate(options);
    }

    /** Select the option with the minimum expected utility, as in BedDeM. */
    public Option selectOption(List<? extends Option> options) {
        if (options == null || options.isEmpty())
            throw new IllegalArgumentException("Options list cannot be empty");
        Map<Option, Double> eu = evaluate(options);
        Option best = null; double min = Double.MAX_VALUE;
        for (Option opt : options) {
            double u = eu.get(opt);
            if (u < min) { min = u; best = opt; }
        }
        return best;
    }
}

class TIBExample {
    // Sion -> Sierre (18 km), Car / Train / Bike.
    // All 13 weights are supplied from outside, exactly as in the BedDeM model:
    // the eight base determinants are each constructed with their weight, and the
    // five aggregate weights are passed straight into the TIBModel constructor.
    // We only set weights + per-option values; TIBModel does the whole calculation
    // (normalise each determinant, weight, aggregate up the tree, pick minimum EU).
    public static void main(String[] args) {
        // Base determinants (leaf), each carrying its own weight.
        LeafDeterminant price = new LeafDeterminant("price", 2.0);
        LeafDeterminant time  = new LeafDeterminant("time", 4.0);
        LeafDeterminant norm  = new LeafDeterminant("norm", 3.0);
        LeafDeterminant role  = new LeafDeterminant("role", 2.0);
        LeafDeterminant self  = new LeafDeterminant("self", 3.0);
        LeafDeterminant emotion = new LeafDeterminant("emotion", 1.0);
        LeafDeterminant facilitating = new LeafDeterminant("facilitating", 2.0);
        LeafDeterminant freq  = new LeafDeterminant("freq", 3.0);

        // Aggregate weights, also supplied from outside.
        TIBModel tib = new TIBModel(
            price, time, norm, role, self, emotion, facilitating, freq,
            4.0,   // attitude
            2.0,   // social
            2.0,   // affect
            4.0,   // intention
            3.0);  // habit

        String[] p = {"price","time","norm","role","self","emotion","facilitating","freq"};
        //                          price time norm role self emo facil freq
        SimpleOption car   = option("Car",   p, new double[]{4.0, 0.3, 2, 3, 1, 1, 0, 0});
        SimpleOption train = option("Train", p, new double[]{3.0, 0.2, 1, 2, 2, 2, 1, 0});
        SimpleOption bike  = option("Bike",  p, new double[]{0.0, 1.0, 3, 1, 3, 3, 0, 1});
        List<Option> options = Arrays.asList(car, train, bike);

        // Inputs
        System.out.println("Sion -> Sierre (18 km): Car / Train / Bike\n");
        System.out.printf("  %-14s%8s%8s%8s%n", "determinant", "Car", "Train", "Bike");
        for (String d : p)
            System.out.printf("  %-14s%8.2f%8.2f%8.2f%n", d,
                car.getProperty(d), train.getProperty(d), bike.getProperty(d));

        // Let the block do the calculation.
        Map<Option, Double> eu = tib.evaluateOptions(options);
        System.out.println("\nExpected utility (computed by the TIB block):");
        for (Option o : options)
            System.out.printf("  %-6s %.4f%n", o.getName() + ":", eu.get(o));
        System.out.println("\nChosen: " + tib.selectOption(options).getName());
    }

    private static SimpleOption option(String name, String[] props, double[] vals) {
        SimpleOption o = new SimpleOption(name);
        for (int i = 0; i < props.length; i++) o.setProperty(props[i], vals[i]);
        return o;
    }
}
