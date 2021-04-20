package eu.h2020.helios_social.core.info_control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.info_control.classifier.Classification;
import eu.h2020.helios_social.core.info_control.classifier.MultiClassNaiveBayes;

/**
 * ContextClassifier detects context of received message.
 * It uses the @link MultiClassNaiveBayes classifier to classify message text content based on the
 * training data from @link MessageContextDatabase.
 * @see InfoControl class, which implements information overload control.
 **/
public class ContextClassifier {

    private final MultiClassNaiveBayes classifier;
    private final MyContexts myContexts;

    public ContextClassifier(MyContexts myContexts, int maxHistorySize) {
        this.classifier = new MultiClassNaiveBayes(maxHistorySize);
        this.myContexts = myContexts;
    }

    public ContextClassifier(MyContexts myContexts) {
        this(myContexts, 500);
    }

    /**
     * Trains the model with all the MessageContexts in the training database (MessageContextDatabase)
     * @param repository the MessageContextRepository
     */
    public void train(MessageContextRepository repository) {
        Collection<MessageContext> messageContextHistoryList = repository.getMessages();
        for(MessageContext messageContext: messageContextHistoryList) {
            String contextId = messageContext.getContextID();
            String from = messageContext.getFrom();
            String topic = messageContext.getMessageTopic();
            String messageText = messageContext.getMessageText();
            train(contextId, from, topic, messageText);
        }
    }

    /**
     * Trains the model with contextual message information: from, topic and text
     * @param contextId
     * @param from
     * @param topic
     * @param text
     */
    public void train(String contextId, String from, String topic, String text) {
        String content = ((from == null) ? "" : from + " ") +
                ((topic == null) ? "" : topic + " ") + ((text == null) ? "" : text);
        if(!content.isEmpty()){
            classifier.addSample(contextId, content);
        }
    }

    /**
     * Trains the model with a MessageContext as a training sample
     * @param messageContext
     */
    public void train(MessageContext messageContext) {
        this.train(messageContext.getContextID(), messageContext.getFrom(),
                messageContext.getMessageTopic(), messageContext.getMessageText());
    }

    /**
     * Classifies a received message into context classes with probability
     * @param from
     * @param topic
     * @param text
     * @return
     */
    public List<ContextProbability> classify(String from, String topic, String text) {
        ArrayList<ContextProbability> contextProbabilities = new ArrayList<ContextProbability>();
        String content = ((from == null) ? "" : from + " ") +
                ((topic == null) ? "" : topic + " ") + ((text == null) ? "" : text);
        if(!content.isEmpty()){
            Collection<Classification> contextClassifications = classifier.classify(content);
            if(contextClassifications != null) {
                for(Classification contextClassification : contextClassifications) {
                    String contextId = contextClassification.getCategory();
                    double prob = contextClassification.getProbability();
                    Context context = myContexts.getContextById(contextId);
                    if(context != null) {
                        contextProbabilities.add(new ContextProbability(context, prob));
                    }
                }
            }
        }
        return contextProbabilities;
    }

}