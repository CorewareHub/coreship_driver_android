package com.coreware.coreshipdriver.util;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public final class BroadcastUtil {

    // broadcast actions
    public static final String ACTION_AUTHENTICATION_COMPLETE = "action_authentication_complete";
    public static final String ACTION_ERROR_LOGOUT = "action_error_logout";
    public static final String ACTION_ERROR_MESSAGE = "action_error_message";
    public static final String ACTION_FORCE_UPDATE = "action_force_update";
    public static final String ACTION_NETWORK_STATUS_CHANGED = "action_network_status_changed";
    public static final String ACTION_NO_INTERNET = "action_no_internet";

    // broadcast keys
    public static final String BROADCAST_KEY_ERROR_MESSAGE = "key_error_message";
    public static final String BROADCAST_KEY_SOURCE_ACTION = "key_source_action";
    public static final String BROADCAST_KEY_UPDATE_MESSAGE = "key_update_message";

    private BroadcastUtil() {
        // Cannot instantiate class
    }


    public static void sendAuthenticationCompleteBroadcast(Context context) {
        Intent broadcast = new Intent(ACTION_AUTHENTICATION_COMPLETE);
        sendBroadcast(context, broadcast);
    }

    public static void sendErrorLogoutBroadcast(Context context, String errorMessage) {
        Intent broadcast = new Intent(ACTION_ERROR_LOGOUT);
        broadcast.putExtra(BROADCAST_KEY_ERROR_MESSAGE, errorMessage);
        sendBroadcast(context, broadcast);
    }

    public static void sendErrorMessageBroadcast(Context context, String errorMessage, String sourceAction) {
        Intent broadcast = new Intent(ACTION_ERROR_MESSAGE);
        broadcast.putExtra(BROADCAST_KEY_ERROR_MESSAGE, errorMessage);
        if (sourceAction != null) {
            broadcast.putExtra(BROADCAST_KEY_SOURCE_ACTION, sourceAction);
        }
        sendBroadcast(context, broadcast);
    }

    public static void sendForceUpdateBroadcast(Context context, String updateMessage) {
        Intent broadcast = new Intent(ACTION_FORCE_UPDATE);
        broadcast.putExtra(BROADCAST_KEY_UPDATE_MESSAGE, updateMessage);
        sendBroadcast(context, broadcast);
    }

    public static void sendNetworkStatusChangedBroadcast(Context context) {
        Intent broadcast = new Intent(ACTION_NETWORK_STATUS_CHANGED);
        sendBroadcast(context, broadcast);
    }

    public static void sendNoInternetBroadcast(Context context) {
        Intent broadcast = new Intent(ACTION_NO_INTERNET);
        sendBroadcast(context, broadcast);
    }


    /* Private methods */

    private static void sendBroadcast(Context context, Intent broadcast) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

}
