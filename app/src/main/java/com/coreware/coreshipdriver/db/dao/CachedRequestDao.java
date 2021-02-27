package com.coreware.coreshipdriver.db.dao;

import com.coreware.coreshipdriver.db.entities.CachedRequest;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CachedRequestDao {

    @Query("SELECT * FROM CachedRequest")
    LiveData<List<CachedRequest>> getCachedRequests();

    @Query("SELECT * FROM CachedRequest WHERE objectId = :objectId AND requestAction = :requestAction")
    LiveData<CachedRequest> getByObjectIdAndRequestAction(String objectId, String requestAction);

    @Update
    void update(CachedRequest...cachedRequests);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedRequest cachedRequest);

    @Delete
    void delete(CachedRequest...cachedRequests);


    @Query("DELETE FROM CachedRequest")
    void deleteAll();

}
