package com.coreware.coreshipdriver.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public final class DeviceUtil {
    private static final String LOG_TAG = DeviceUtil.class.getName();

    private DeviceUtil() {
        // Cannot instantiate class
    }

    public static String getAndroidOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG,"There was a problem getting the app version name", e);
            return "";
        }
    }

    public static Integer getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG,"There was a problem getting the app version code", e);
            return 0;
        }
    }

    /**
     * Gets the device localization code. This combines the language code and region code if both exist.
     *
     * @return
     */
    public static String getLocalizationCode() {
        String languageCode = Locale.getDefault().getLanguage();
        if (languageCode.length() > 0) {
            String regionCode = getRegionCode();
            if (regionCode.length() > 0) {
                return languageCode + "-" + regionCode;
            }
            return languageCode;
        }
        return "";
    }

    /**
     * Gets the device region code
     *
     * @return
     */
    public static String getRegionCode() {
        return Locale.getDefault().getCountry();
    }

    /**
     * Returns the app version with the third decimal place stripped off.
     * Example: Version 3.2.1 would return 3.2
     *
     * @param context
     * @return A shortened version of the app version number or an empty string if the version number
     * isn't found.
     */
    public static String getShortenedAppVersion(Context context) {
        String fullAppVersion = getAppVersion(context);
        if (fullAppVersion.length() == 0) {
            return "";
        } else {
            int lastDecimalIndex = fullAppVersion.lastIndexOf(".");
            return fullAppVersion.substring(0, lastDecimalIndex);
        }
    }

}
