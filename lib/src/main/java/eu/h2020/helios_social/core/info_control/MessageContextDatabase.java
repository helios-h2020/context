package eu.h2020.helios_social.core.info_control;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MessageContextDatabase provides functionality to save, load, modify
 * and search MessageContext information.
 */
@Database(entities = {MessageContext.class}, version = 1, exportSchema = false)
public abstract class MessageContextDatabase extends RoomDatabase {

    public abstract MessageContextDao messageContextDao();

    private static volatile MessageContextDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MessageContextDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MessageContextDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MessageContextDatabase.class, "message_context_history_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

