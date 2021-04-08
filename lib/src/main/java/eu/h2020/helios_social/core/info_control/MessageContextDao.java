package eu.h2020.helios_social.core.info_control;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * MessageContextDao provides database operations interface
 */
@Dao
public interface MessageContextDao {

    @Insert
    void insert(MessageContext messageContext);

    @Delete
    void delete(MessageContext messageContext);

    @Query("DELETE FROM message_context_table")
    void deleteAll();

    @Query("SELECT * FROM message_context_table")
    List<MessageContext> getMessages();

    @Query("SELECT * FROM message_context_table WHERE context=:contextId AND sender=:from")
    List<MessageContext> getMessagesByContextFrom(String contextId, String from);

    @Query("SELECT * FROM message_context_table WHERE context=:contextId")
    List<MessageContext> getMessagesByContext(String contextId);

    @Query("SELECT * FROM message_context_table WHERE sender=:from")
    List<MessageContext> getMessagesByFrom(String from);

    @Query("DELETE FROM message_context_table where id NOT IN (SELECT id from message_context_table ORDER BY id DESC LIMIT :maxSize)")
    void shrinkDb(int maxSize);
}
