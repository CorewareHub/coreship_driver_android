package com.coreware.coreshipdriver.api.coreware;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardApiVolleyImpl extends CorewareAPI {
    private static final String LOG_TAG = DashboardApiVolleyImpl.class.getName();

    public JSONObject clockIn(final Application application) {
        // build URL
        String url = buildActionUrl(application, ACTION_CLOCK_IN);

        try {
            // build request params
            JSONObject paramsJson = buildClockInParams(application);

            // send request
            return sendRequest(application, url, paramsJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not clock in.\n" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public JSONObject clockOut(final Application application) {
        // build URL
        String url = buildActionUrl(application, ACTION_CLOCK_OUT);

        try {
            // build request params
            JSONObject paramsJson = buildClockOutParams(application);

            // send request
            return sendRequest(application, url, paramsJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not clock out.\n" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public JSONObject isClockedIn(final Application application) {
        // build URL
        String url = buildActionUrl(application, ACTION_IS_CLOCKED_IN);

        try {
            // build request params
            JSONObject paramsJson = buildIsClockedInParams(application);

            // send request
            return sendRequest(application, url, paramsJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not check if clocked in.\n" + e.getLocalizedMessage(), e);
            return null;
        }
    }


    /* Parameter Build Methods */

    private JSONObject buildClockInParams(final Application application)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        addRequiredRequestParams(application, paramsJson, true);

        return paramsJson;
    }

    private JSONObject buildClockOutParams(final Application application)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        addRequiredRequestParams(application, paramsJson, true);

        return paramsJson;
    }

    private JSONObject buildIsClockedInParams(final Application application)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        addRequiredRequestParams(application, paramsJson, true);

        return paramsJson;
    }

}
