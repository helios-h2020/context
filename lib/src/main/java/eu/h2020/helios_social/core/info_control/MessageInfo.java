package eu.h2020.helios_social.core.info_control;

/**
 * MessageInfo
 * - incoming message data
 */
public class MessageInfo {

    private String id; // id, can be used by the application
    private String from; // sender
    private long timestamp; // message received
    private int importance; // importance of the message (if included into the received message)
    private String contextId; // context associated with the message (if any)
    private String messageTopic; // if available
    private String messageText;  // if available    // message topic ja content   may be combined?

    /**
     * Creates a MessageInfo
     * @param id the identifier, can be used by applications to identify this
     * @param from the sender
     * @param timestamp the timestamp when the message received
     * @param importance
     * @param contextId
     * @param messageTopic
     * @param messageText
     */
    public MessageInfo(String id, String from, long timestamp, int importance, String contextId, String messageTopic, String messageText) {
        this.id = id;
        this.from = from;
        this.timestamp = timestamp;
        this.importance = importance;
        this.contextId = contextId;
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

    public String getContextId() {
        return contextId;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public String getMessageText() {
        return messageText;
    }
}
