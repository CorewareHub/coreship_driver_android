package com.coreware.coreshipdriver.api.coreware;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationApiVolleyImpl extends CorewareAPI {
    private static final String LOG_TAG = AuthenticationApiVolleyImpl.class.getName();

    public JSONObject getUserProfile(final Application application) {
        // build URL
        String url = buildActionUrl(application, ACTION_GET_USER_PROFILE);

        try {
            // build request params
            JSONObject paramsJson = buildGetUserProfileParams(application);

            // send request
            return sendRequest(application, url, paramsJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not get user profile.\n" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public JSONObject login(final Application application, String username, String password) {
        // build URL
        String url = buildActionUrl(application, ACTION_LOGIN);

        JSONObject paramsJson;
        try {
            // build request params
            paramsJson = buildLoginParams(application, username, password);

            return sendRequest(application, url, paramsJson);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not build login params.\n" + e.getLocalizedMessage(), e);
            return null;
        }

        // create request
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, paramsJson, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
////                Log.i(LOG_TAG, "Successfully logged in: " + response);
//                // TODO do something
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(LOG_TAG, "Error logging in: " + error.getLocalizedMessage(), error);
//            }
//        });
//
//        // send request
//        sendRequest(application, jsonObjectRequest);
    }

    public void logout(final Application application) {
        // build URL
        String url = buildActionUrl(application, ACTION_LOGOUT);

        JSONObject paramsJson;
        try {
            // build request params
            paramsJson = buildLogoutParams(application);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not build logout params.\n" + e.getLocalizedMessage(), e);
            return;
        }

        // create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, paramsJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.i(LOG_TAG, "Successfully logged out: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error logging out: " + error.getLocalizedMessage(), error);
            }
        });

        // send request
        sendRequest(application, jsonObjectRequest);
    }


    /* Private Methods */

    private JSONObject buildGetUserProfileParams(final Application application)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        addRequiredRequestParams(application, paramsJson, true);

        return paramsJson;
    }

    private JSONObject buildLoginParams(final Application application, String username, String password)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        paramsJson.put(REQUEST_KEY_USERNAME, username);
        paramsJson.put(REQUEST_KEY_PASSWORD, password);

        addRequiredRequestParams(application, paramsJson, false);

        return paramsJson;
    }

    private JSONObject buildLogoutParams(final Application application)
            throws JSONException, PackageManager.NameNotFoundException {
        JSONObject paramsJson = new JSONObject();

        addRequiredRequestParams(application, paramsJson, true);

        return paramsJson;
    }

}
