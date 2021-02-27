package com.coreware.coreshipdriver.db.dao;

import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.db.entities.SessionAndUser;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public interface SessionDao {

    @Transaction
    @Query("SELECT * from Session LIMIT 1")
    SessionAndUser getSessionsAndUsers();

    @Query("SELECT * from Session")
    List<Session> getSessions();

    @Update
    void update(Session...sessions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Session session);

    @Delete
    void delete(Session...sessions);

    @Query("DELETE FROM Session")
    void deleteAll();

}
