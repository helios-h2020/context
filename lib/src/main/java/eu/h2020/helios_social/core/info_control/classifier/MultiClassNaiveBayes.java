package eu.h2020.helios_social.core.info_control.classifier;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Multi-class Naive Bayes text classifier
 */
public class MultiClassNaiveBayes implements Serializable {

    private final ProbabilityTable categoryFeatureTable;
    private final LinkedList<TrainSample> samplesQueue;
    private final HashMap<String, Integer> categoryTable;

    private final int maxSamples;
    private final int maxTextLength;
    private final double alpha;

    private static final boolean filterStopWords = true;

    /**
     * Creates MultiClassNaiveBayes classifier
     */
    public MultiClassNaiveBayes() {
        this(500,2000, 1.0);
    }

    /**
     * Create MultiClassNaiveBayes classifier
     * @param maxSamples the maximum number of training samples
     */
    public MultiClassNaiveBayes(int maxSamples) {
        this(maxSamples,2000, 1.0);
    }

    /**
     * Create MultiClassNaiveBayes classifier
     * @param maxSamples the maximum number of training samples
     * @param maxTextLength the maximum length of input text for the classifier
     */
    public MultiClassNaiveBayes(int maxSamples, int maxTextLength) {
        this(maxSamples, maxTextLength, 1.0);
    }

    /**
     * Creates MultiClassNaiveBayes classifier
     * @param maxSamples the maximum number of training samples
     * @param maxTextLength the maximum length of input text for the classifier
     * @param alpha the additional smoothing term. For the so-called Lidstone smoothing alpha < 1.0 and for Laplace smoothing alpha == 1.
     */
    public MultiClassNaiveBayes(int maxSamples, int maxTextLength, double alpha) {
        this.categoryFeatureTable = new ProbabilityTable();
        this.samplesQueue = new LinkedList<TrainSample>();
        this.categoryTable = new HashMap<String, Integer>();

        this.maxSamples = maxSamples;
        this.maxTextLength = maxTextLength;
        this.alpha = alpha;
    }

    private static class TrainSample {
        String category;
        String[] features;
        public TrainSample(String category, String[] features) {
            this.category = category;
            this.features = features;
        }
    }

    /**
     * Adds training sample for the classifier
     * @param category the classified category of the text. It corresponds to context id
     * @param text the input text.
     */
    public void addSample(@NonNull String category, @NonNull String text) {
        String[] preprocessedText = Classification.preprocessText(text, maxTextLength, filterStopWords);
        addSample(category, preprocessedText);
    }

    private void addSample(String category, String[] features) {
        TrainSample sample = new TrainSample(category, features);
        samplesQueue.add(sample);
        for (String feature : features) {
            categoryFeatureTable.increase(category, feature);
        }
        Integer count = categoryTable.get(category);
        categoryTable.put(category, count != null ? count + 1 : 1);
        if(samplesQueue.size() > maxSamples) {
            sample = samplesQueue.pop();
            removeSample(sample);
        }
    }

    private void removeSample(TrainSample sample) {
        samplesQueue.remove(sample);
        Integer count = categoryTable.get(sample.category);
        if(count == null || count == 0) {
            categoryTable.remove(sample.category);
        } else {
            categoryTable.put(sample.category, count - 1);
        }
        for (String feature : sample.features) {
            categoryFeatureTable.decrease(sample.category, feature);
        }
    }

    private int getCategoryCount(String category) {
        Integer count = categoryTable.get(category);
        return count != null ? count : 0;
    }

    /**
     * Removes a category from the training data
     * @param category the category (i.e. context id)
     */
    public void removeCategory(@NonNull String category) {
        ArrayList<TrainSample> removedSamples = new ArrayList<TrainSample>();
        for(TrainSample sample : samplesQueue) {
            if(sample.category.equals(category)) {
                removedSamples.add(sample);
            }
        }
        for(TrainSample sample : removedSamples) {
            removeSample(sample);
        }
    }

    /**
     * Classifies the input text
     * @param text the input text to be classified
     * @return the list of possible classifications with probability values
     */
    public List<Classification> classify(@NonNull String text) {
        return getCategoryProbabilities(Classification.preprocessText(text, maxTextLength, filterStopWords));
    }

    // Basic Multinominal Naive Bayes
    private List<Classification> getCategoryProbabilitiesNB(@NonNull String[] features) {
        List<Classification> classifications = new ArrayList<Classification>();
        Set<String> categories = categoryFeatureTable.getRows(); // all categories 
        double alphaD = alpha*categoryFeatureTable.getColumns().size(); // alpha * total number of features

        for(String category : categories) {
            double probability = 1.0;
            int featureCount = categoryFeatureTable.getRowSum(category); // total count of features in category
            for (String feature : features) {
                int value = categoryFeatureTable.get(category, feature);
                probability *= (value + alpha) / (featureCount + alphaD);    //  P(feature|category)
            }
            int categoryCount = getCategoryCount(category); // count of this category in samples
            probability *= categoryCount;
            classifications.add(new Classification(category, probability));
        }
        Classification.normalize(classifications);
        classifications.sort((o1, o2) -> Double.compare(o2.getProbability(), o1.getProbability()));
        return classifications;
    }

    // adapted, modified NB, probabilities calculated so that only the matches in features are strongly weighted
    private List<Classification> getCategoryProbabilities(@NonNull String[] features) {
        List<Classification> classifications = new ArrayList<Classification>();
        Set<String> categories = categoryFeatureTable.getRows(); // all categories

        for(String category : categories) {
            double probability = 0.0;
            int featureCount = categoryFeatureTable.getRowSum(category); // total count of features in category
            for (String feature : features) {
                int value = categoryFeatureTable.get(category, feature);
                if(value > 0) {
                    probability += value;
                }
            }
            probability /= featureCount;
            classifications.add(new Classification(category, probability));
        }
        Classification.normalize(classifications);
        classifications.sort((o1, o2) -> Double.compare(o2.getProbability(), o1.getProbability()));
        return classifications;
    }

    /**
     * Resets the classifier into initial state
     */
    public void reset() {
        categoryFeatureTable.reset();
        categoryTable.clear();
        samplesQueue.clear();
    }
}