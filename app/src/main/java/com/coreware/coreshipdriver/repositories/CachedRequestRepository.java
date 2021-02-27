package com.coreware.coreshipdriver.repositories;

import android.app.Application;

import com.coreware.coreshipdriver.db.CoreShipDatabase;
import com.coreware.coreshipdriver.db.dao.CachedRequestDao;
import com.coreware.coreshipdriver.db.entities.CachedRequest;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * Code based on https://developer.android.com/codelabs/android-room-with-a-view#7
 */

public class CachedRequestRepository {

    private CachedRequestDao mCachedRequestDao;
    private LiveData<List<CachedRequest>> mAllCachedRequests;

    public CachedRequestRepository(Application application) {
        CoreShipDatabase db = CoreShipDatabase.getDatabase(application);
        mCachedRequestDao = db.cachedRequestDao();
        mAllCachedRequests = mCachedRequestDao.getCachedRequests();
    }

    public List<CachedRequest> getAll() {
        return mAllCachedRequests.getValue();
    }

    public CachedRequest getByObjectIdAndRequestAction(String objectId, String requestAction) {
        return mCachedRequestDao.getByObjectIdAndRequestAction(objectId, requestAction).getValue();
    }

    /**
     * Inserts the cached request into the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param cachedRequest
     */
    public void insert(CachedRequest cachedRequest) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mCachedRequestDao.insert(cachedRequest);
        });
    }

    /**
     * Updates the cached request in the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param cachedRequest
     */
    public void update(CachedRequest cachedRequest) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mCachedRequestDao.update(cachedRequest);
        });
    }

    /**
     * Deletes the cached request from the database
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param cachedRequest
     */
    public void delete(CachedRequest cachedRequest) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mCachedRequestDao.delete(cachedRequest);
        });
    }

}
