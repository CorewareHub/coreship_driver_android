package com.coreware.coreshipdriver.api.services;

import android.app.Application;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coreware.coreshipdriver.api.coreware.AuthenticationApiVolleyImpl;
import com.coreware.coreshipdriver.api.coreware.CorewareAPI;
import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.db.entities.SessionAndUser;
import com.coreware.coreshipdriver.db.entities.User;
import com.coreware.coreshipdriver.repositories.SessionRepository;
import com.coreware.coreshipdriver.repositories.UserRepository;
import com.coreware.coreshipdriver.util.BroadcastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;

public class AuthenticationApiIntentService extends CorewareIntentService {
    private static final String LOG_TAG = AuthenticationApiIntentService.class.getName();

    /* Intent Service Actions */
    private class Action {
        static final String GET_USER_PROFILE = "com.coreware.coreshipdriver.api.services.action.GET_USER_PROFILE";
        static final String LOGIN = "com.coreware.coreshipdriver.api.services.action.LOGIN";
        static final String LOGOUT = "com.coreware.coreshipdriver.api.services.action.LOGOUT";
    }

    /* Intent Service Extras */
    private class Extra {
        static final String PASSWORD = "com.coreware.coreshipdriver.api.services.extra.password";
        static final String USERNAME = "com.coreware.coreshipdriver.api.services.extra.username";
    }

    private final AuthenticationApiVolleyImpl mAuthenticationApi = new AuthenticationApiVolleyImpl();


    /**
     * Starts this service to perform action Get User Profile with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     */
    public static void startActionGetUserProfile(Context context) {
//        Log.i(LOG_TAG, "startActionLogin()");
        Intent intent = new Intent(context, AuthenticationApiIntentService.class);
        intent.setAction(Action.GET_USER_PROFILE);
        startService(context, intent);
    }

    /**
     * Starts this service to perform action Login with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     * @param username
     * @param password
     */
    public static void startActionLogin(Context context, String username, String password) {
//        Log.i(LOG_TAG, "startActionLogin()");
        Intent intent = new Intent(context, AuthenticationApiIntentService.class);
        intent.setAction(Action.LOGIN);
        intent.putExtra(Extra.USERNAME, username);
        intent.putExtra(Extra.PASSWORD, password);
        startService(context, intent);
    }

    /**
     * Starts this service to perform action Login with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     *
     * @param context
     */
    public static void startActionLogout(Context context) {
        Intent intent = new Intent(context, CorewareIntentService.class);
        intent.setAction(Action.LOGOUT);
        startService(context, intent);
    }


    private static void startService(Context context, Intent intent) {
        enqueueWork(context, AuthenticationApiIntentService.class, API_JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String intentAction = intent.getAction();
        if (Action.GET_USER_PROFILE.equals(intentAction)) {
            handleActionGetUserProfile();
        } else if (Action.LOGIN.equals(intentAction)) {
            final String username = intent.getStringExtra(Extra.USERNAME);
            final String password = intent.getStringExtra(Extra.PASSWORD);
            handleActionLogin(username, password);
        } else if (Action.LOGOUT.equals(intentAction)) {
            handleActionLogout();
        }
    }


    /* Handle Action */

    private void handleActionGetUserProfile() {
        JSONObject response = mAuthenticationApi.getUserProfile(getApplication());
        if (response != null) {
            try {
                // determine if the result of the request is 'OK'
                boolean successfulRequest = wasRequestSuccessful(response);
                if (!successfulRequest) {
                    String errorMessage = getErrorMessage(response);
                    Log.e(LOG_TAG, "Error: Login response returned: " + errorMessage);
                    BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), errorMessage, Action.LOGIN);
                    return;
                }

                // handle successful response


                BroadcastUtil.sendAuthenticationCompleteBroadcast(getApplicationContext());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from Get User Profile response: " + e.getLocalizedMessage(), e);
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem getting the user profile: " + e.getLocalizedMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), e.getLocalizedMessage(), Action.GET_USER_PROFILE);
            }
        }
    }

    private void handleActionLogin(String username, String password) {
        JSONObject response = mAuthenticationApi.login(getApplication(), username, password);
        if (response != null) {
            try {
                // determine if the result of the request is 'OK'
                boolean successfulRequest = wasRequestSuccessful(response);
                if (!successfulRequest) {
                    String errorMessage = getErrorMessage(response);
                    Log.e(LOG_TAG, "Error: Login response returned: " + errorMessage);
                    BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), errorMessage, Action.LOGIN);
                    return;
                }

                // handle successful response
                handleLoginResponse(response);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from the login response: " + e.getLocalizedMessage(), e);
                // TODO handle exception
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem logging in: " + e.getLocalizedMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), e.getLocalizedMessage(), Action.LOGIN);
            }
        }
    }

    private void handleActionLogout() {
        mAuthenticationApi.logout(getApplication());
    }


    /* Handle Response */

    private void handleGetUserProfileResponse(JSONObject responseJson) throws Exception {
        Application application = getApplication();
        SessionRepository sessionRepository = new SessionRepository(application);
        UserRepository userRepository = new UserRepository(application);

        JSONObject userProfileJson = responseJson.getJSONObject(CorewareAPI.RESPONSE_KEY_USER_PROFILE);

        String address1 = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_ADDRESS_1);
        String address2 = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_ADDRESS_2);
        String city = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_CITY);
        Integer countryId = userProfileJson.getInt(CorewareAPI.RESPONSE_KEY_COUNTRY_ID);
        String emailAddress = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_EMAIL_ADDRESS);
        String firstName = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_FIRST_NAME);
        String lastName = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_LAST_NAME);
        JSONArray phoneNumbers = userProfileJson.getJSONArray(CorewareAPI.RESPONSE_KEY_PHONE_NUMBERS);
        String postalCode = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_POSTAL_CODE);
        String state = userProfileJson.getString(CorewareAPI.RESPONSE_KEY_STATE);

        SessionAndUser currentSessionAndUser = sessionRepository.getCurrentSessionAndUser();
        if (currentSessionAndUser == null) {
            Log.e(LOG_TAG, "No session found. No user logged in.");
            BroadcastUtil.sendErrorLogoutBroadcast(getApplicationContext(), "Could not find user's session. Logging out.");
        }

        // Update user with update user profile
        User user = currentSessionAndUser.getUser();
        user.setAddress1(address1);
        user.setAddress2(address2);
        user.setCity(city);
        user.setCountryId(countryId);
        user.setEmailAddress(emailAddress);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        String phoneNumber;
        if (phoneNumbers.length() > 0) {
            if (phoneNumbers.get(0) instanceof JSONObject) {
                JSONObject phoneNumberJson = (JSONObject) phoneNumbers.get(0);
                phoneNumber = phoneNumberJson.getString(CorewareAPI.RESPONSE_KEY_PHONE_NUMBER);
            } else {
                phoneNumber = null;
            }
        } else {
            phoneNumber = null;
        }
        user.setPhoneNumber(phoneNumber);
        user.setPostalCode(postalCode);
        user.setState(state);
        user.setUsername(emailAddress);

        userRepository.update(user);

        BroadcastUtil.sendAuthenticationCompleteBroadcast(getApplicationContext());
    }

    private void handleLoginResponse(JSONObject responseJson) throws Exception {
//        Log.i(LOG_TAG, "handleLoginResponse()");
//        Log.i(LOG_TAG, responseJson.toString());
        try {
            String result = responseJson.getString(CorewareAPI.RESPONSE_KEY_RESULT);

            if (result.equals(CorewareAPI.RESPONSE_VALUE_RESULT_ERROR)) {
                // handle error response
                String errorMessage = responseJson.getString(CorewareAPI.RESPONSE_KEY_ERROR_MESSAGE);
                Log.e(LOG_TAG, "Error: Login response returned: " + errorMessage);
                BroadcastUtil.sendErrorMessageBroadcast(getApplicationContext(), errorMessage, Action.LOGIN);
                return;
            } else if (result.equals(CorewareAPI.RESPONSE_VALUE_RESULT_OK)) {
                // handle successful response

                // get response values
                Long userId = responseJson.getLong(CorewareAPI.RESPONSE_KEY_USER_ID);
                String sessionId = responseJson.getString(CorewareAPI.RESPONSE_KEY_SESSION_ID);

                // initiate repositories
                SessionRepository sessionRepository = new SessionRepository(getApplication());
                UserRepository userRepository = new UserRepository(getApplication());

                // create session
                Session session = new Session();
                session.setSessionId(sessionId);
                session.setDateLoggedIn(new Date());
                sessionRepository.insert(session);

                // get User
                User user = userRepository.getByUserId(userId);

                // if User does not exist, create user
                if (user == null) {
                    user = new User();
                    user.setUserId(userId);
                    user.setConnectedSessionId(session.getSessionId());
                    userRepository.insert(user);
                } else {
                    // if user does exist, update session id
                    user.setConnectedSessionId(session.getSessionId());
                    userRepository.update(user);
                }

                // get the user's profile
                handleActionGetUserProfile();
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error getting data from login response: " + e.getLocalizedMessage(), e);
        }
    }

}
