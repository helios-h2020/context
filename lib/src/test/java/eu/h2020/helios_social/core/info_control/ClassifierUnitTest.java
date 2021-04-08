package eu.h2020.helios_social.core.info_control;

import org.junit.Test;

import java.util.List;

import eu.h2020.helios_social.core.info_control.classifier.Classification;
import eu.h2020.helios_social.core.info_control.classifier.MultiClassNaiveBayes;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierUnitTest {

    @Test
    public void classifierTest() {
        MultiClassNaiveBayes classifier = new MultiClassNaiveBayes(100, 1000);

        final String test1 = "Test, classification!";
        final String test2 = "Helios1 test";
        final String test3 = "Hello helios";
        final String test_input = "a b c hello1 test, helios?";

        // Add training samples
        classifier.addSample("context1", test1);
        classifier.addSample("context2", test2);
        classifier.addSample("helios", test3);

        // Remove a category (context2) from the training database
        classifier.removeCategory("context2");

        // Finally, perform the classification, i.e., the prediction of context probabilities
        List<Classification> classifications = classifier.classify(test_input);

        for(Classification c : classifications) {
            System.out.println("probability:" + c.getCategory() + "," + c.getProbability());
        }

    }
}