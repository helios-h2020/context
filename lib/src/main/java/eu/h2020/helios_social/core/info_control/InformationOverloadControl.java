package eu.h2020.helios_social.core.info_control;

import java.util.List;

import eu.h2020.helios_social.core.context.Context;

/**
 * InformationOverloadControl provides interface methods for
 * the information overload control implementations.
 *
 * Information overload control can internally limit the amount of incoming message alerts for the user,
 * depending on the state of the user’s momentary ability and willingness to process incoming messages.
 * Users are encouraged to read primarily those messages that are relevant in a particular context.
 * In particular, it determines the importance of the received message in the user's context.
 * E.g., context detection of received messages, message read reaction time, trust of sender in
 * context via contextual ego network can be used in determining the importance of received
 * messages.
 *
 * @see InfoControl
 */
public interface InformationOverloadControl {

    /**
     * Estimates context probabilities for a message
     * @param message the message
     * @return the iterator to ContextProbability list
     */
    List<ContextProbability> getContextProbabilities(MessageInfo message);

    /**
     * Gets message importance in a context.
     * @param message the message
     * @param context the context
     * @return the importance value, i.e., an integer value between 1 (very low) - 5 (very high),  0 undefined
     */
    int getMessageImportance(MessageInfo message, Context context);

    /**
     * Get message importances in all user contexts
     * @param message the message
     * @return the list of MessageImportances, which contains (Context, importance) pairs
     */
    List<MessageImportance> getMessageImportance(MessageInfo message);

    /**
     * Notifies InformationOverloadControl about a read message.
     * The read message information is then stored into the MessageContextDatabase.
     * @param message the read message (MessageInfo)
     */
    void readMessage(MessageInfo message);

    /**
     * Adds a MessageContext (read message) into database.
     * The added message is used as training data of the Information Overload Control's
     * context classifier.
     *
     * @param messageContext the MessageContext
     */
    void addMessageContext(MessageContext messageContext);
}
