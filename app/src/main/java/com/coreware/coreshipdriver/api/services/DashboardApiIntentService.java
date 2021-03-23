package com.coreware.coreshipdriver.api.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coreware.coreshipdriver.api.coreware.CorewareAPI;
import com.coreware.coreshipdriver.api.coreware.DashboardApiVolleyImpl;
import com.coreware.coreshipdriver.db.entities.User;
import com.coreware.coreshipdriver.repositories.UserRepository;
import com.coreware.coreshipdriver.util.BroadcastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

public class DashboardApiIntentService extends CorewareIntentService {
    private static final String LOG_TAG = DashboardApiIntentService.class.getName();

    /* Intent Service Actions */
    private class Action {
        static final String CLOCK_IN = "com.coreware.coreshipdriver.api.services.action.CLOCK_IN";
        static final String CLOCK_OUT = "com.coreware.coreshipdriver.api.services.action.CLOCK_OUT";
        static final String IS_CLOCKED_IN = "com.coreware.coreshipdriver.api.services.action.IS_CLOCKED_IN";
    }

    /* Intent Service Extras */
    private class Extra {
//        static final String PASSWORD = "com.coreware.coreshipdriver.api.services.extra.password";
    }

    private final DashboardApiVolleyImpl mDashboardApi = new DashboardApiVolleyImpl();


    /**
     * Starts this service to perform action Clock In with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     */
    public static void startActionClockIn(Context context) {
//        Log.i(LOG_TAG, "startActionClockIn()");
        Intent intent = new Intent(context, DashboardApiIntentService.class);
        intent.setAction(Action.CLOCK_IN);
        startService(context, intent);
    }

    /**
     * Starts this service to perform action Clock Out with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     */
    public static void startActionClockOut(Context context) {
//        Log.i(LOG_TAG, "startActionClockOut()");
        Intent intent = new Intent(context, DashboardApiIntentService.class);
        intent.setAction(Action.CLOCK_OUT);
        startService(context, intent);
    }

    /**
     * Starts this service to perform action Is Clocked In with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     */
    public static void startActionIsClockedIn(Context context) {
//        Log.i(LOG_TAG, "startActionIsClockedIn()");
        Intent intent = new Intent(context, DashboardApiIntentService.class);
        intent.setAction(Action.IS_CLOCKED_IN);
        startService(context, intent);
    }


    private static void startService(Context context, Intent intent) {
        enqueueWork(context, DashboardApiIntentService.class, API_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String intentAction = intent.getAction();
        if (Action.CLOCK_IN.equals(intentAction)) {
            handleActionClockIn();
        } else if (Action.CLOCK_OUT.equals(intentAction)) {
            handleActionClockOut();
        } else if (Action.IS_CLOCKED_IN.equals(intentAction)) {
            handleActionIsClockedIn();
        }
    }


    /* Handle Action */

    private void handleActionClockIn() {
        JSONObject response = mDashboardApi.clockIn(getApplication());
        if (response != null) {
            try {
                // determine if the result of the request is 'OK'
                boolean successfulRequest = wasRequestSuccessful(response);
                if (!successfulRequest) {
                    String errorMessage = getErrorMessage(response);
                    Log.e(LOG_TAG, "Error: Clock in response returned: " + errorMessage);
                    BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), errorMessage, Action.CLOCK_IN);
                    return;
                }

//                Log.i(LOG_TAG, "Clock in successful");
//                Log.i(LOG_TAG, response.toString());

                // handle successful response
                handleClockInResponse(response);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from the clock in response: " + e.getLocalizedMessage(), e);
                // TODO handle exception
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem clocking in: " + e.getLocalizedMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), e.getLocalizedMessage(), Action.CLOCK_IN);
            }
        }
    }

    private void handleActionClockOut() {
        JSONObject response = mDashboardApi.clockOut(getApplication());
        if (response != null) {
            try {
                // determine if the result of the request is 'OK'
                boolean successfulRequest = wasRequestSuccessful(response);
                if (!successfulRequest) {
                    String errorMessage = getErrorMessage(response);
                    Log.e(LOG_TAG, "Error: Clock out response returned: " + errorMessage);
                    BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), errorMessage, Action.CLOCK_OUT);
                    return;
                }

//                Log.i(LOG_TAG, "Clock out successful");
//                Log.i(LOG_TAG, response.toString());

                // handle successful response
                handleClockOutResponse(response);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from the clock out response: " + e.getLocalizedMessage(), e);
                // TODO handle exception
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem clocking out: " + e.getLocalizedMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), e.getLocalizedMessage(), Action.CLOCK_OUT);
            }
        }
    }

    private void handleActionIsClockedIn() {
        JSONObject response = mDashboardApi.isClockedIn(getApplication());
        if (response != null) {
            try {
                Log.i(LOG_TAG, "Is clocked in check successful");
                Log.i(LOG_TAG, response.toString());

                // handle successful response
                handleIsClockedInResponse(response);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from the is clocked in response: " + e.getLocalizedMessage(), e);
                // TODO handle exception
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem checking is clocked in: " + e.getLocalizedMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), e.getLocalizedMessage(), Action.IS_CLOCKED_IN);
            }
        }
    }


    /* Handle Response */

    private void handleClockInResponse(JSONObject responseJson) throws Exception {
        UserRepository userRepository = new UserRepository(getApplication());
        User currentUser = userRepository.getCurrentUser();
        currentUser.setClockedIn(true);
        userRepository.update(currentUser);
    }

    private void handleClockOutResponse(JSONObject responseJson) throws Exception {
        UserRepository userRepository = new UserRepository(getApplication());
        User currentUser = userRepository.getCurrentUser();
        currentUser.setClockedIn(false);
        userRepository.update(currentUser);
    }

    private void handleIsClockedInResponse(JSONObject responseJson) throws Exception {
        if (responseJson != null && responseJson.has(CorewareAPI.RESPONSE_KEY_RESULT)) {
            UserRepository userRepository = new UserRepository(getApplication());
            User currentUser = userRepository.getCurrentUser();
            currentUser.setClockedIn(responseJson.getString(CorewareAPI.RESPONSE_KEY_RESULT).equals(CorewareAPI.RESPONSE_VALUE_RESULT_YES));
            userRepository.update(currentUser);
        }
    }

}
