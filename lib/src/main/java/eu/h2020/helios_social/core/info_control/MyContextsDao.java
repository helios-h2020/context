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
public interface MyContextsDao {

    @Insert
    void add(MyContextsEntity contextEntity);

    @Delete
    void remove(MyContextsEntity contextEntity);

    @Query("DELETE FROM mycontexts_table")
    void removeAll();

    @Query("SELECT * FROM mycontexts_table")
    List<MyContextsEntity> getContexts();

    @Query("SELECT * FROM mycontexts_table WHERE context_id=:contextId")
    MyContextsEntity getContextById(String contextId);

    @Query("DELETE FROM mycontexts_table where context_id NOT IN (SELECT context_id from mycontexts_table ORDER BY context_id DESC LIMIT :maxSize)")
    void shrinkDb(int maxSize);
}
