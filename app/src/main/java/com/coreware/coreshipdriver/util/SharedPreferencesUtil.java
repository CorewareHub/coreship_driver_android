package com.coreware.coreshipdriver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

public final class SharedPreferencesUtil {
    private static final String LOG_TAG = SharedPreferencesUtil.class.getName();

    /* Keys */
    private static final String KEY_DEVICE_ID = "key_device_id";
    private static final String KEY_TESTING_MODE = "key_testing_mode";

    private SharedPreferencesUtil() {
        // Cannot instantiate class
    }


    /* Get methods */

    public static String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString(KEY_DEVICE_ID, null);
        if (deviceId == null) {
            createDeviceId(context);
            deviceId = getDeviceId(context);
        }
        return deviceId;
    }

    public static boolean isAppInTestingMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_TESTING_MODE, false);
    }


    /* Save methods */

    public static void saveIsAppInTestingMode(Context context, boolean isInTestingMode) {
        saveBooleanPreference(context, KEY_TESTING_MODE, isInTestingMode);
    }


    /* Private methods */

    private static void createDeviceId(Context context) {
        String newDeviceId = UUID.randomUUID().toString();
        saveStringPreference(context, KEY_DEVICE_ID, newDeviceId);
    }

    private static void saveBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static void saveStringPreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value == null) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.apply();
    }

}
