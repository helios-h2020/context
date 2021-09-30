package eu.h2020.helios_social.core.info_control;

import org.junit.Test;

import java.util.List;

import eu.h2020.helios_social.core.info_control.classifier.Classification;
import eu.h2020.helios_social.core.info_control.classifier.MultiClassNaiveBayes;

/**
 * Context classifier local unit test
 *
 * @see ContextClassifier
 * @see MultiClassNaiveBayes
 */
public class ClassifierUnitTest {

    @Test
    public void classifierTest() {
        MultiClassNaiveBayes classifier = new MultiClassNaiveBayes(100, 1000);

        // training sample texts
        final String test1 = "Test, classification!";
        final String test2 = "Helios1 test";
        final String test3 = "Hello helios";
        final String test_input = "a b c hello test, helios?";

        // Add training samples and associate target contexts with them
        classifier.addSample("context1", test1);
        classifier.addSample("context2", test2);
        classifier.addSample("helios", test3);

        // Remove a category (context2) from the training database
        classifier.removeCategory("context2");

        // Finally, perform the classification, i.e., the prediction of context probabilities
        List<Classification> classifications = classifier.classify(test_input);

        // List context probabilities for the samples
        for(Classification c : classifications) {
            System.out.println("probability:" + c.getCategory() + "," + c.getProbability());
        }

    }
}