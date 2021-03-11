package com.coreware.coreshipdriver.api.services;

import com.coreware.coreshipdriver.api.coreware.CorewareAPI;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.JobIntentService;

public abstract class CorewareIntentService  extends JobIntentService {

    protected static Integer API_JOB_ID = 100;


    protected boolean wasRequestSuccessful(JSONObject response) throws JSONException {
        if (response != null && response.has(CorewareAPI.RESPONSE_KEY_RESULT)) {
            if (response.getString(CorewareAPI.RESPONSE_KEY_RESULT).equals(CorewareAPI.RESPONSE_VALUE_RESULT_OK)) {
                if (response.has(CorewareAPI.RESPONSE_VALUE_RESULT_ERROR)) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    protected String getErrorMessage(JSONObject response) throws JSONException {
        if (response.has(CorewareAPI.RESPONSE_KEY_ERROR_MESSAGE)) {
            return response.getString(CorewareAPI.RESPONSE_KEY_ERROR_MESSAGE);
        }

        return "";
    }

}
