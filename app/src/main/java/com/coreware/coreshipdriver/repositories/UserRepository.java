package com.coreware.coreshipdriver.repositories;

import android.app.Application;

import com.coreware.coreshipdriver.db.CoreShipDatabase;
import com.coreware.coreshipdriver.db.dao.UserDao;
import com.coreware.coreshipdriver.db.entities.User;

/**
 * Code based on https://developer.android.com/codelabs/android-room-with-a-view#7
 */

public class UserRepository {

    private UserDao mUserDao;

    public UserRepository(Application application) {
        CoreShipDatabase db = CoreShipDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    public User getByUserId(Long userId) {
        return mUserDao.getByUserId(userId).getValue();
    }

    public User getCurrentUser() {
        return mUserDao.getCurrentUser();
    }

    /**
     * Inserts the user into the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param user
     */
    public void insert(User user) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.insert(user);
        });
    }

    /**
     * Updates the user in the database.
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param user
     */
    public void update(User user) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.update(user);
        });
    }

    /**
     * Deletes the user from the database
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     *
     * @param user
     */
    public void delete(User user) {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.delete(user);
        });
    }

    /**
     * Deletes all of the user from the database
     *
     * You must call this on a non-UI thread or your app will throw an exception. Room ensures
     * that you're not doing any long running operations on the main thread, block the UI.
     */
    public void deleteAll() {
        CoreShipDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.deleteAll();
        });
    }

}
