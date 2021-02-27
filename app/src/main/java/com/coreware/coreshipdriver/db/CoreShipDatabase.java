package com.coreware.coreshipdriver.db;

import android.content.Context;

import com.coreware.coreshipdriver.db.dao.CachedRequestDao;
import com.coreware.coreshipdriver.db.dao.SessionDao;
import com.coreware.coreshipdriver.db.dao.UserDao;
import com.coreware.coreshipdriver.db.entities.CachedRequest;
import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.db.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Code based on https://developer.android.com/codelabs/android-room-with-a-view#6
 */

@Database(entities = {CachedRequest.class, Session.class, User.class}, version = 1, exportSchema = false)
@TypeConverters({RoomConverters.class})
public abstract class CoreShipDatabase extends RoomDatabase {

    public abstract CachedRequestDao cachedRequestDao();
    public abstract SessionDao sessionDao();
    public abstract UserDao userDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile CoreShipDatabase INSTANCE;


    public static CoreShipDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CoreShipDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CoreShipDatabase.class, "coreship-db")
                            .fallbackToDestructiveMigration() // will reset the database when the schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
