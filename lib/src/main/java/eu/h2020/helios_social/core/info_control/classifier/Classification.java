package eu.h2020.helios_social.core.info_control.classifier;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Classification result container. Binds category (context) with probability.
 */
public class Classification {

    private final String category;
    private double probability;

    // a set of common "stop words", which can be filtered out of input text for text classification
    public static final HashSet<String> STOP_WORDS = new HashSet<String>(Arrays.asList("a","able","about","across",
            "after","all","almost","also","am","among","an","and", "any","are","as","at","be", "because","been",
            "but","by","can","cannot","could","did","do","does","either",
            "else","ever","every", "for","from","get","got","had","has","have","he","her","hers","him","his","how",
            "however","i","if","in", "into","is","it","its","just","least","let","like","likely","may","me","might",
            "most","must","my","neither","no","nor","not","of","off", "often","on","only","or","other","our","own",
            "rather","said","say","says","she","should","since","so","some","than","that","the","their", "them","then",
            "there","these","they","this","to","too","us","wants","was","we","were","what","when","where","which","while",
            "who","whom","why","will","with","would","yet","you","your"));

    /**
     * Creates a Classification instance
     * @param category
     * @param probability
     */
    public Classification(String category, double probability) {
        this.category = category;
        this.probability = probability;
    }

    public String getCategory() {
        return category;
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
    public static void normalize(List<Classification> probabilities) {
        double sum = 0.0;
        for(Classification classification : probabilities) {
            sum += classification.getProbability();
        }
        if (sum > 0.0) {
            for (Classification classification: probabilities) {
                classification.setProbability(classification.getProbability()/sum);
            }
        } else if (probabilities.size() > 0) {
            double probability = 1.0/probabilities.size();
            for (Classification classification: probabilities) {
                classification.setProbability(probability);
            }
        }
    }

    public static String[] preprocessText(@NonNull String text, int maxTextLength, boolean filterStopWords) {
        String inputText = text.length() > maxTextLength ? text.substring(0,maxTextLength) : text;
        String[] processedText = inputText.toLowerCase().split("[\\s,.;:!?\"]+");
        if(filterStopWords) {
            List<String> featuresList = new ArrayList<String>(Arrays.asList(processedText));
            featuresList.removeIf(Classification.STOP_WORDS::contains);
            processedText = featuresList.toArray(new String[0]);
        }
        return processedText;
    }
}
