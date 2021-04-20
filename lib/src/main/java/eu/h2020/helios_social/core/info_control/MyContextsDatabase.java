package eu.h2020.helios_social.core.info_control;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * MessageContextDatabase provides functionality to save, load, modify
 * and search MessageContext information.
 */
@Database(entities = {MyContextsEntity.class}, version = 1, exportSchema = false)
public abstract class MyContextsDatabase extends RoomDatabase {

    public abstract MyContextsDao myContextsDao();

    private static volatile MyContextsDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MyContextsDatabase getDatabase(final android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (MyContextsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyContextsDatabase.class, "mycontexts_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

