package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;

import eu.h2020.helios_social.core.context.Context;

/**
 * MessageImportance class
 * - associates importance value in context to message
 */
public class MessageImportance {
    private final ContextProbability contextProbability;
    private final int importance;

    // message importance value constants
    public static final int IMPORTANCE_UNKNOWN = 0;
    public static final int IMPORTANCE_VERY_LOW = 1;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MEDIUM = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_VERY_HIGH = 5;

    /**
     * Creates a MessageImportance
     * @param contextProbability the context's probability
     * @param importance the importance value
     */
    public MessageImportance(@NonNull ContextProbability contextProbability, int importance) {
        this.contextProbability = contextProbability;
        this.importance = importance;
    }

    /**
     * Returns context
     * @return the context
     */
    public Context getContext() {return contextProbability.getContext(); }

    /**
     * Returns importance value
     * @return the importance
     */
    public int getImportance() {
        return importance;
    }

    /**
     * Returns context probability
     * @return the probability value
     */
    public double getContextProbability() { return contextProbability.getProbability(); }

    /**
     * Returns importance level value based on the importance value on scale [0,1]
     * @param importanceValue
     * @return
     */
    public static int messageImportanceLevel(double importanceValue) {
        if(importanceValue < 0.0) {
            return IMPORTANCE_UNKNOWN;
        } else if(importanceValue < 0.2) {
            return IMPORTANCE_VERY_LOW;
        } else if(importanceValue < 0.4) {
            return IMPORTANCE_LOW;
        } else if(importanceValue < 0.6) {
            return IMPORTANCE_MEDIUM;
        } else if(importanceValue < 0.8) {
            return IMPORTANCE_HIGH;
        } else {
            return IMPORTANCE_VERY_HIGH;
        }
    }

}
