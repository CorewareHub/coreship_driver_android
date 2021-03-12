package com.coreware.coreshipdriver.ui.activities;

import android.os.Bundle;

import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.repositories.SessionRepository;
import com.coreware.coreshipdriver.util.IntentLaunchUtil;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchScreenActivity extends AppCompatActivity {
    private static final String LOG_TAG = LaunchScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        loadInitialView();
    }


    /* Private methods */

    private void loadInitialView() {
        // Using a Thread because the database interaction needs it
        new Thread(new Runnable() {
            @Override
            public void run() {
                SessionRepository sessionRepository = new SessionRepository(getApplication());

                Session currentSession = sessionRepository.getCurrentSession();
                if (currentSession != null) {
                    // if logged in, launch Main Activity
                    IntentLaunchUtil.launchMainActivity(LaunchScreenActivity.this);
                } else {
                    // if not logged in, launch Login Activity
                    IntentLaunchUtil.launchLoginActivity(LaunchScreenActivity.this);
                }

                LaunchScreenActivity.this.finish();
            }
        }).start();
    }
}