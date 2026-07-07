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

/** Convenience builder for the eight base determinants and five aggregate weights. */
class TIBBuilder {
    private final Map<String, Double> base = new HashMap<>();
    private final Map<String, Double> agg  = new HashMap<>();
    private static final String[] BASE = {
        "belief","evaluation","norm","role","selfConcept","emotion","facilitating","frequency" };

    public TIBBuilder() {
        for (String b : BASE) base.put(b, 1.0);
        agg.put("attitude", 2.0); agg.put("social", 3.0); agg.put("affect", 2.5);
        agg.put("intention", 4.0); agg.put("habit", 1.5);
    }
    /** Set one of the eight known base-determinant weights. */
    public TIBBuilder setBaseWeight(String determinant, double weight) {
        if (!base.containsKey(determinant))
            throw new IllegalArgumentException("Unknown base determinant: " + determinant);
        base.put(determinant, weight); return this;
    }
    public TIBBuilder setAggregateWeight(String determinant, double weight) {
        if (!agg.containsKey(determinant))
            throw new IllegalArgumentException("Unknown aggregate determinant: " + determinant);
        agg.put(determinant, weight); return this;
    }
    public TIBModel build() {
        return new TIBModel(
            new LeafDeterminant("belief",      base.get("belief")),
            new LeafDeterminant("evaluation",  base.get("evaluation")),
            new LeafDeterminant("norm",        base.get("norm")),
            new LeafDeterminant("role",        base.get("role")),
            new LeafDeterminant("selfConcept", base.get("selfConcept")),
            new LeafDeterminant("emotion",     base.get("emotion")),
            new LeafDeterminant("facilitating",base.get("facilitating")),
            new LeafDeterminant("frequency",   base.get("frequency")),
            agg.get("attitude"), agg.get("social"), agg.get("affect"),
            agg.get("intention"), agg.get("habit"));
    }
}

class TIBExample {
    public static void main(String[] args) {
        TIBModel tib = new TIBBuilder()
            .setAggregateWeight("attitude", 2.0)
            .setAggregateWeight("social", 3.0)
            .setAggregateWeight("affect", 2.5)
            .setAggregateWeight("intention", 4.0)
            .setAggregateWeight("habit", 1.5)
            .build();

        String[] p = {"belief","evaluation","norm","role","selfConcept","emotion","facilitating","frequency"};
        SimpleOption car   = option("car",   p, new double[]{1.0, 4.0, 2.0, 3.0, 2.0, 4.0, 1.0, 10.0});
        SimpleOption train = option("train", p, new double[]{0.9, 3.0, 1.0, 2.0, 1.0, 3.5, 1.0,  5.0});
        SimpleOption bike  = option("bike",  p, new double[]{1.1, 0.0, 3.0, 1.0, 3.0, 3.0, 0.8,  2.0});

        List<Option> options = Arrays.asList(car, train, bike);
        System.out.println("Chosen: " + tib.selectOption(options).getName());
        Map<Option, Double> eu = tib.evaluateOptions(options);
        System.out.println("Expected utilities (lower = better):");
        for (Option o : options)
            System.out.printf("  %s: %.3f%n", o.getName(), eu.get(o));
    }

    private static SimpleOption option(String name, String[] props, double[] vals) {
        SimpleOption o = new SimpleOption(name);
        for (int i = 0; i < props.length; i++) o.setProperty(props[i], vals[i]);
        return o;
    }
}
