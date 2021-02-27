package com.coreware.coreshipdriver.util;

import android.app.Activity;
import android.content.Intent;

import com.coreware.coreshipdriver.ui.activities.LoginActivity;
import com.coreware.coreshipdriver.ui.activities.MainActivity;

public final class IntentLaunchUtil {

    private IntentLaunchUtil() {
        // Cannot instantiate class
    }

    public static void launchLoginActivity(Activity launchingActivity) {
        Intent intent = new Intent(launchingActivity, LoginActivity.class);
        launchingActivity.startActivity(intent);
    }

    public static void launchMainActivity(Activity launchingActivity) {
        Intent intent = new Intent(launchingActivity, MainActivity.class);
        launchingActivity.startActivity(intent);
    }

}
