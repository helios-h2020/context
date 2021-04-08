package eu.h2020.helios_social.core.info_control;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * MessageContext class represents a message with an associated context. MessageContexts are
 * saved into MessageContextDatabase, which is used by the InformationOverloadControl.
 */
@Entity(tableName = "message_context_table")
public class MessageContext {
    @PrimaryKey(autoGenerate = true)
    int id;
    @NonNull
    @ColumnInfo(name = "context")
    String contextID;   // user active context when read the message
    @ColumnInfo(name = "sender")
    String from;
    @ColumnInfo(name = "timestamp")
    long timestamp; // message received
    @ColumnInfo(name = "reaction_time")
    int reactionTime;     // user read
    @ColumnInfo(name = "importance")
    int importance; // if known when read.  range 1-5.   or 0 if not defined
    @ColumnInfo(name = "trust")
    float trust;  // trust value of sender in context (if Available), or -1.0 if not defined
    @ColumnInfo(name = "topic")
    String messageTopic; // if available
    @ColumnInfo(name = "message_text")
    String messageText;  // if available    // message topic ja content   may be combined?


    /**
     * Creates a MessageContext
     * @param contextID
     * @param from
     * @param timestamp
     * @param reactionTime
     * @param importance
     * @param trust
     * @param messageTopic
     * @param messageText
     */
    public MessageContext(@NonNull String contextID, String from, long timestamp, int reactionTime, int importance, float trust, String messageTopic, String messageText) {
        this.contextID = contextID;
        this.from = from;
        this.timestamp = timestamp;
        this.reactionTime = reactionTime;
        this.importance = importance;
        this.trust = trust;
        this.messageTopic = messageTopic;
        this.messageText = messageText;
    }

    @NonNull
    public String getContextID() {
        return contextID;
    }

    public String getFrom() {
        return from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getReactionTime() {
        return reactionTime;
    }

    public int getImportance() {
        return importance;
    }

    public float getTrust() {
        return trust;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public String getMessageText() {
        return messageText;
    }

}
