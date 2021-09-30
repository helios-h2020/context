package eu.h2020.helios_social.core.info_control;

import java.util.List;

import eu.h2020.helios_social.core.context.Context;

/**
 * ContextProbability class
 * - associates probability to context
 */
public class ContextProbability {
    private final Context context;
    private double probability;

    /**
     * Creates a ContextProbability
     * @param context the context
     * @param probability the probability
     */
    public ContextProbability(Context context, double probability) {
        this.context = context;
        this.probability = probability;
    }

    public Context getContext() {
        return context;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * Normalize probabilities, i.e. the sum of probabilities in the list is 1
     * @param probabilities
     */
    public static void normalize(List<ContextProbability> probabilities) {
        double sum = 0.0;
        for(ContextProbability probability : probabilities) {
            sum += probability.getProbability();
        }
        if (sum > 0.0) {
            for (ContextProbability probability : probabilities) {
                probability.setProbability(probability.getProbability()/sum);
            }
        } else if (probabilities.size() > 0) {
            double prob = 1.0/probabilities.size();
            for (ContextProbability probability : probabilities) {
                probability.setProbability(prob);
            }
        }
    }

}
