package eu.h2020.helios_social.core.info_control;

import java.util.List;

/**
 * MessageInfo
 * - incoming message data
 */
public class MessageInfo {

    private final String id; // id, can be used by the application
    private final String from; // sender
    private final long timestamp; // message received
    private final int importance; // importance of the message (if included into the received message, otherwise 0)
    private final List<String> contextIds; // list of contexts associated with the message (if any)
    private final String messageTopic; // if available
    private final String messageText;  // if available    // message topic ja content   may be combined?

    /**
     * Creates a MessageInfo
     * @param id the identifier, can be used by applications to identify this
     * @param from the sender
     * @param timestamp the timestamp when the message received
     * @param importance
     * @param contextIds
     * @param messageTopic
     * @param messageText
     */
    public MessageInfo(String id, String from, long timestamp, int importance, List<String> contextIds, String messageTopic, String messageText) {
        this.id = id;
        this.from = from;
        this.timestamp = timestamp;
        this.importance = importance;
        this.contextIds = contextIds;
        this.messageTopic = messageTopic;
        this.messageText = messageText;
    }

    public MessageInfo(String id, String from, long timestamp, String messageTopic, String messageText) {
        this(id, from, timestamp, 0, null, messageTopic, messageText);
    }

    public MessageInfo(String from, long timestamp, String messageTopic, String messageText) {
        this(null, from, timestamp, 0, null, messageTopic, messageText);
    }

    /**
     * Returns identifier of this
     * @return the identifier
     */
    public String getId() { return id; }

    public String getFrom() {
        return from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getImportance() {
        return importance;
    }

    /**
     * Returns lists of contexts (ids)
     * @return the context list
     */
    public List<String> getContextIds() {
        return contextIds;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public String getMessageText() {
        return messageText;
    }
}
