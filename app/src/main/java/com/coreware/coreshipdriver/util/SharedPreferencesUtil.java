package com.coreware.coreshipdriver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coreware.coreshipdriver.camera.CameraSizePair;
import com.coreware.coreshipdriver.camera.GraphicOverlay;
import com.google.android.gms.common.images.Size;
import com.google.mlkit.vision.barcode.Barcode;

import java.util.List;
import java.util.UUID;

/**
 * The camera preference util functions are based on
 * https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/settings/PreferenceUtils.kt
 */
public final class SharedPreferencesUtil {
    private static final String LOG_TAG = SharedPreferencesUtil.class.getName();

    /* Keys */
    private static final String KEY_AUTO_SEARCH_ENABLED = "key_auto_search_enabled";
    private static final String KEY_BARCODE_RETICLE_HEIGHT= "key_barcode_reticle_height";
    private static final String KEY_BARCODE_RETICLE_WIDTH = "key_barcode_reticle_width";
    private static final String KEY_DELAY_LOADING_BARCODE_RESULT= "key_delay_loading_barcode_result";
    private static final String KEY_DEVICE_ID = "key_device_id";
    private static final String KEY_ENABLE_BARCODE_SIZE_CHECK = "key_enable_barcode_size_check";
    private static final String KEY_MINIMUM_BAR_CODE_WIDTH = "key_minimum_bar_code_width";
    private static final String KEY_REAR_CAMERA_PICTURE_SIZE = "key_rear_camera_picture_size";
    private static final String KEY_REAR_CAMERA_PREVIEW_SIZE = "key_rear_camera_preview_size";
    private static final String KEY_TESTING_MODE = "key_testing_mode";

    private SharedPreferencesUtil() {
        // Cannot instantiate class
    }


    /* Get methods */

    public static RectF getBarcodeReticleBox(Context context, GraphicOverlay overlay) {
        float overlayWidth = overlay.getWidth();
        float overlayHeight = overlay.getHeight();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int barcodeReticleWidth = sharedPreferences.getInt(KEY_BARCODE_RETICLE_WIDTH, 80);
        int barcodeReticleHeight = sharedPreferences.getInt(KEY_BARCODE_RETICLE_HEIGHT, 35);
        float boxWidth = overlayWidth * barcodeReticleWidth / 100;
        float boxHeight = overlayHeight * barcodeReticleHeight / 100;
        float cx = overlayWidth / 2;
        float cy = overlayHeight / 2;
        return new RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2);
    }

    public static String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString(KEY_DEVICE_ID, null);
        if (deviceId == null) {
            createDeviceId(context);
            deviceId = getDeviceId(context);
        }
        return deviceId;
    }

    public static Float getProgressToMeetBarcodeSizeRequirement(Context context, GraphicOverlay overlay, Barcode barcode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableBarcodeSizeCheck = sharedPreferences.getBoolean(KEY_ENABLE_BARCODE_SIZE_CHECK, false);
        if (enableBarcodeSizeCheck) {
            float reticleBoxWidth = getBarcodeReticleBox(context, overlay).width();
            float barcodeWidth = overlay.translateX(barcode.getBoundingBox().width());
            float requiredWidth = reticleBoxWidth * sharedPreferences.getInt(KEY_MINIMUM_BAR_CODE_WIDTH, 80) / 100;
            float value = barcodeWidth / requiredWidth;
            if (value < 1) {
                return value;
            } else {
                return 1f;
            }
        } else {
            return 1f;
        }
    }

    public static CameraSizePair getUserSpecifiedPreviewSize(Context context, Camera camera) {
        Log.i(LOG_TAG, "getUserSpecifiedPreviewSize");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String rearCameraPreviewSizeSetting = sharedPreferences.getString(KEY_REAR_CAMERA_PREVIEW_SIZE, null);
        String rearCameraPictureSizeSetting = sharedPreferences.getString(KEY_REAR_CAMERA_PICTURE_SIZE, null);
        if (rearCameraPreviewSizeSetting == null) {
            return null;
        }
        Size rearCameraPreviewSize = Size.parseSize(rearCameraPreviewSizeSetting);
        Size rearCameraPictureSize = Size.parseSize(rearCameraPictureSizeSetting);
        return new CameraSizePair(rearCameraPreviewSize, rearCameraPictureSize);
    }

    public static boolean isAppInTestingMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_TESTING_MODE, false);
    }

    public static boolean isAutoSearchEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_AUTO_SEARCH_ENABLED, true);
    }

    public static boolean shouldDelayLoadingBarcodeResult(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_DELAY_LOADING_BARCODE_RESULT, false);
    }


    /* Save methods */

    public static void saveRearCameraPictureSize(Context context, String picture) {
        Log.i(LOG_TAG, "saveRearCameraPictureSize");
        saveStringPreference(context, KEY_REAR_CAMERA_PICTURE_SIZE, picture);
    }

    public static void saveRearCameraPreviewSize(Context context, String preview) {
        Log.i(LOG_TAG, "saveRearCameraPreviewSize");
        saveStringPreference(context, KEY_REAR_CAMERA_PREVIEW_SIZE, preview);
    }

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
