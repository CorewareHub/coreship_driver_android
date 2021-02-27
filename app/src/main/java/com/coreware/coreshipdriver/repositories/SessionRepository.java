package com.coreware.coreshipdriver.repositories;


import android.app.Application;

import com.coreware.coreshipdriver.db.CoreShipDatabase;
import com.coreware.coreshipdriver.db.dao.SessionDao;
import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.db.entities.SessionAndUser;

import java.util.List;

/**
 *  Code based on https://developer.android.com/codelabs/android-room-with-a-view#7
 */

public class SessionRepository {

    private SessionDao mSessionDao;

    public SessionRepository(Application application) {
        CoreShipDatabase db = CoreShipDatabase.getDatabase(application);
        mSessionDao = db.sessionDao();
    }

    public Session getCurrentSession() {
        List<Session> sessions = mSessionDao.getSessions();
        if (sessions == null || sessions.size() == 0) {
            return null;
        }
        return sessions.get(0); // there should only be one session
    }

    public SessionAndUser getCurrentSessionAndUser() {
        return mSessionDao.getSessionsAndUsers();
    }

    /**
     * Inserts the session into the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param session
     */
    public void insert(Session session) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.insert(session);
        });
    }

    /**
     * Updates the session in the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param session
     */
    public void update(Session session) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.update(session);
        });
    }

    /**
     * Deletes the session from the database
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param session
     */
    public void delete(Session session) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.delete(session);
        });
    }

    /**
     * Deletes all of the sessions from the database
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     */
    public void deleteAll() {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mSessionDao.deleteAll();
        });
    }

}
