package com.coreware.coreshipdriver.db.dao;

import com.coreware.coreshipdriver.db.entities.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User WHERE userId = :userId")
    LiveData<User> getByUserId(Long userId);

    @Query("SELECT * FROM User LIMIT 1")
    User getCurrentUser();

    @Query("SELECT * FROM User LIMIT 1")
    LiveData<User> getLiveDataCurrentUser();

    @Update
    void update(User...users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Delete
    void delete(User...users);

    @Query("DELETE FROM User")
    void deleteAll();

}
