package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;
import eu.h2020.helios_social.core.contextualegonetwork.ContextualEgoNetwork;

/**
 * InfoControl class implements the InformationOverloadControl interface.
 * @see InformationOverloadControl
 */
public class InfoControl implements InformationOverloadControl {

    private final ContextClassifier classifier;
    private final ContextualEgoNetwork cen;
    private final MyContexts myContexts;
    private final MessageContextRepository repository;

    // default weights. Their values are in range [0.0,1.0], and the sum of weights should be 1
    double context_weight = 0.6;
    double trust_weight = 0.2;
    double reactiontime_weight = 0.15;
    double importance_weight = 0.1;
    double number_weight = 0.05;

    /**
     * Creates an InfoControl class instance
     *
     * @param myContexts the container of user contexts list
     * @param repository the repository of messages
     */
    public InfoControl(@NonNull MyContexts myContexts, MessageContextRepository repository) {
        this.myContexts = myContexts;
        this.cen = myContexts.getCen();
        this.classifier = new ContextClassifier(myContexts);
        this.repository = repository;
    }

    /**
     * Estimates context probabilities for a message
     * @param messageInfo the message
     * @return the ContextProbability list
     */
    @Override
    public List<ContextProbability> getContextProbabilities(MessageInfo messageInfo) {
        List<ContextProbability> contextProbabilities = new ArrayList<ContextProbability>();
        String from = messageInfo.getFrom();
        String topic = messageInfo.getMessageTopic();
        String content = messageInfo.getMessageText();
        String contextId = messageInfo.getContextId();

        if (contextId != null) {
            Context sharedContext = myContexts.getContextById(contextId);
            if (sharedContext != null) { // message is associated with a shared context
                contextProbabilities.add(new ContextProbability(sharedContext, 1.0));
                return contextProbabilities;
            }
        }
        List<Context> contexts = cen != null ? CenUtils.getContexts(cen, from) : null;

        if (contexts != null && contexts.size() > 0) {
            for (Context context : contexts) {
                contextProbabilities.add(new ContextProbability(context, 1.0));
            }
        } else {
            // apply machine learning to detect context
            contextProbabilities = classifier.classify(from, topic, content);
            ContextProbability.normalize(contextProbabilities);
        }
        fillMyContextsProbabilities(contextProbabilities);

        return contextProbabilities;
    }

    private void fillMyContextsProbabilities(List<ContextProbability> contextProbabilities) {
        for(Context context: myContexts.getContexts()) {
            boolean found = false;
            for(ContextProbability probability : contextProbabilities) {
                if(context == probability.getContext()) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                contextProbabilities.add(new ContextProbability(context, 0.0));
            }
        }
    }

    /**
     * Adds the MessageContext data into the training database for information overload control.
     * @param messageContext
     */
    public void addMessageContext(MessageContext messageContext) {
        classifier.train(messageContext);
        repository.insert(messageContext);
    }

    /**
     * Gets message importance in a context.
     * @param messageInfo the message
     * @param context the context
     * @return the importance value
     */
    @Override
    public int getMessageImportance(@NonNull MessageInfo messageInfo, @NonNull Context context) {
        if (messageInfo.getImportance() != MessageImportance.IMPORTANCE_UNKNOWN) {
            return messageInfo.getImportance(); // Importance is already associated with the message
        }
        // Get context probabilities
        double contextVal = 0.0;
        List<ContextProbability> contextProbabilities = getContextProbabilities(messageInfo);
        for (ContextProbability contextProbability : contextProbabilities) {
            if (contextProbability.getContext() == context) {
                   return getMessageImportance(messageInfo, contextProbability);
            }
        }
        return MessageImportance.IMPORTANCE_UNKNOWN;
    }


    @Override
    public List<MessageImportance> getMessageImportance(MessageInfo messageInfo) {
        List<MessageImportance> messageImportances = new ArrayList<MessageImportance>();
        // Get context probabilities
        List<ContextProbability> contextProbabilities = getContextProbabilities(messageInfo);
        for (ContextProbability contextProbability : contextProbabilities) {
            messageImportances.add(new MessageImportance(contextProbability.getContext(),
                    getMessageImportance(messageInfo, contextProbability)));
        }
        return messageImportances;
    }

    // TODO
    public void receiveMessage(MessageInfo message) {
        // tallennetaan id + timestamp?
    }

    // TODO
    public void sendMessage(MessageInfo message) {
        // TOTO?
    }

    @Override
    public void readMessage(MessageInfo message) {
        List<Context> activeContexts = myContexts.getActiveContexts();
        long timestamp = System.currentTimeMillis();
        int reactionTime = (int)(timestamp - message.getTimestamp());
        for(Context context: activeContexts) {
            MessageContext messageContext = new MessageContext(context.getId(), message.getFrom(), message.getTimestamp(), reactionTime,
                    message.getImportance(), -1.0f, message.getMessageTopic(), message.getMessageText());
            addMessageContext(messageContext);
        }
    }

    // TODO. Required?
    public void readMessage(MessageInfo messageInfo, long timestamp) {

    }

    public void setWeights(double context_weight, double trust_weight, double reactiontime_weight,
                           double importance_weight, double number_weight) {
        this.context_weight = context_weight;
        this.trust_weight = trust_weight;
        this.reactiontime_weight = reactiontime_weight;
        this.importance_weight = importance_weight;
        this.number_weight = number_weight;
    }

    /**
     * Gets message importance in a context.
     * @param messageInfo the message
     * @param contextProbability the context probability
     * @return the importance value
     */
    private int getMessageImportance(@NonNull MessageInfo messageInfo, @NonNull ContextProbability contextProbability) {

        Context context = contextProbability.getContext();
        double contextVal = contextProbability.getProbability();

        String contextId = context.getId();
        String from = messageInfo.getFrom();

        // the effects of reaction time to importance
        double reactionTimeVal = 1.0;
        double reactionTimeMedianContextFrom = repository.getMedianReactionTime(contextId, from);
        double reactionTimeMedianFrom = repository.getMedianReactionTime(null, from);
        if(reactionTimeMedianContextFrom >= 0 && reactionTimeMedianFrom >=0) {
            reactionTimeVal =  reactionTimeMedianContextFrom / reactionTimeMedianFrom;
        } else {
            double reactionTimeMedianContext = repository.getMedianReactionTime(contextId, null);
            double reactionTimeMedian = repository.getMedianReactionTime(null, null);
            if (reactionTimeMedianContext >= 0 && reactionTimeMedian >= 0) {
                reactionTimeVal = reactionTimeMedianContext / reactionTimeMedian;
            }
        }
        reactionTimeVal = sigmoid(1.0-reactionTimeVal);   // the result is between [0,1]

        // previous message importances
        double importanceVal = 3.0;
        double importanceMedianContextFrom = repository.getMedianImportance(contextId, from);
        if(importanceMedianContextFrom > 0.0) {
            importanceVal = importanceMedianContextFrom;
        } else {
            double importanceMedianFrom = repository.getMedianImportance(null, from);
            if(importanceMedianFrom > 0.0) {
                importanceVal = importanceMedianFrom;
            } else {
                double importanceMedianContext = repository.getMedianImportance(contextId, null);
                if(importanceMedianContext > 0.0) {
                    importanceVal = importanceMedianContext;
                }
            }
        }
        importanceVal = (importanceVal-1.0)/4.0;   // the result is between [0,1]
        /*
        // previous trust
        double trustMedianContextFrom = repository.getMedianTrust(contextId, from);
        double trustMedianContext = repository.getMedianTrust(contextId, null);
        double trustMedianFrom = repository.getMedianTrust(null, from);
        double trustTimeMedian = repository.getMedianTrust(null, null);
        */
        // the effects of trust to message importance
        double trustFrom = CenUtils.getTrust(cen, from, context);
        double trustVal = trustFrom >= 0.0 ? trustFrom : 0.5;     // trustVal is between [0,1]

        // the effects of number of messages to importance
        // numberOfMessagesVal = Number of messages from / Median number of messages from   // TODO
        double numberOfMessagesVal = 0.5;
        double numberOfMessagesContextFrom = repository.getSize(contextId, from);
        double numberOfMessages = repository.getSize(null, null);
        if(numberOfMessages > 2.0) {
            numberOfMessagesVal = Math.min(2.0*numberOfMessagesContextFrom / numberOfMessages, 1.0);
        }
        // calculate the message importance value
        double messageImportanceVal = context_weight*contextVal + trust_weight*trustVal + reactiontime_weight*reactionTimeVal
                + importance_weight*importanceVal + number_weight*numberOfMessagesVal;

        return MessageImportance.messageImportanceLevel(messageImportanceVal);
    }

    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

}
