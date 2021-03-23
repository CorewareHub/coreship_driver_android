package com.coreware.coreshipdriver.api.coreware;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.api.volleyintegration.VolleyRequestQueue;
import com.coreware.coreshipdriver.db.entities.CachedRequest;
import com.coreware.coreshipdriver.db.entities.Session;
import com.coreware.coreshipdriver.repositories.CachedRequestRepository;
import com.coreware.coreshipdriver.repositories.SessionRepository;
import com.coreware.coreshipdriver.util.BroadcastUtil;
import com.coreware.coreshipdriver.util.DeviceUtil;
import com.coreware.coreshipdriver.util.FileUtil;
import com.coreware.coreshipdriver.util.NetworkUtil;
import com.coreware.coreshipdriver.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class CorewareAPI {
    private static final String LOG_TAG = CorewareAPI.class.getName();

    private class CorewareApiURL {
        static final String BASE_PRODUCTION = "https://coreship.coreware.com/";
        static final String BASE_TEST = "https://coreship.coreware.com/";

        class Path {
            static final String API = "api.php";
            static final String FILE = "download.php";
            static final String IMAGE = "getimage.php";
        }
    }

    // Coreware API URL Action
    protected final String ACTION_CLOCK_IN = "clock_in";
    protected final String ACTION_CLOCK_OUT = "clock_out";
    protected final String ACTION_GET_USER_PROFILE = "get_user_profile";
    protected final String ACTION_IS_CLOCKED_IN = "is_clocked_in";
    protected final String ACTION_LOGIN = "login";
    protected final String ACTION_LOGOUT = "logout";

    // API response keys
    public static final String RESPONSE_KEY_ADDRESS_1 = "address_1";
    public static final String RESPONSE_KEY_ADDRESS_2 = "address_2";
    public static final String RESPONSE_KEY_CITY = "city";
    public static final String RESPONSE_KEY_COUNTRY_ID = "country_id";
    public static final String RESPONSE_KEY_EMAIL_ADDRESS = "email_address";
    public static final String RESPONSE_KEY_ERROR_MESSAGE = "error_message";
    public static final String RESPONSE_KEY_FIRST_NAME = "first_name";
    public static final String RESPONSE_KEY_LAST_NAME = "last_name";
    public static final String RESPONSE_KEY_PHONE_NUMBER = "phone_number";
    public static final String RESPONSE_KEY_PHONE_NUMBERS = "phone_numbers";
    public static final String RESPONSE_KEY_POSTAL_CODE = "postal_code";
    public static final String RESPONSE_KEY_RESULT = "result";
    public static final String RESPONSE_KEY_SESSION_ID = "session_identifier";
    public static final String RESPONSE_KEY_STATE = "state";
    private final String RESPONSE_KEY_UPDATE_CONTENT = "update_content";
    public static final String RESPONSE_KEY_USER_ID = "user_id";
    public static final String RESPONSE_KEY_USER_PROFILE = "user_profile";
    public static final String RESPONSE_KEY_USER_TYPE = "user_type";
    public static final String RESPONSE_KEY_USER_TYPE_ID = "user_type_id";

    // API response values
    public static final String RESPONSE_VALUE_RESULT_ERROR = "ERROR";
    private final String RESPONSE_VALUE_RESULT_ERROR_LOGIN_REQUIRED = "Login Required";
    private final String RESPONSE_VALUE_RESULT_FORCE_UPDATE = "UPDATE";
    public static final String RESPONSE_VALUE_RESULT_NO = "NO";
    public static final String RESPONSE_VALUE_RESULT_OK = "OK";
    public static final String RESPONSE_VALUE_RESULT_YES = "YES";

    // API required request keys
    private final String REQUEST_KEY_CONNECTION_KEY = "device_identifier";
    private final String REQUEST_KEY_DEVICE_IDENTIFIER = "device_identifier";
    private final String REQUEST_KEY_LOCALIZATION_CODE = "localization_code";
    private final String REQUEST_KEY_MOBILE_APP_CODE = "api_app_code";
    private final String REQUEST_KEY_MOBILE_APP_VERSION = "api_app_version";
    private final String REQUEST_KEY_SESSION_ID = "session_identifier";

    // API request keys
    protected final String REQUEST_KEY_PASSWORD = "password";
    protected final String REQUEST_KEY_USERNAME = "user_name";

    // API request values
    private final String REQUEST_VALUE_CONNECTION_KEY = "E65F6E43E75486C437503BB3584ADAFA";
    private final String REQUEST_VALUE_MOBILE_APP_CODE = "ANDROID_DRIVERS";

    private final String lineEnd = "\r\n";
    private final String requestBoundary = "*****";
    private final String twoHyphens = "--";
    private int maxBufferSize = 1 * 1024 * 1024;


    protected void addRequiredRequestParams(Application application, JSONObject requestParams, boolean authRequired)
            throws PackageManager.NameNotFoundException, JSONException {
        if (authRequired) {
            Session session = getSession(application);
            if (session != null) {
                requestParams.put(REQUEST_KEY_SESSION_ID, session.getSessionId());
            } else {
                Log.e(LOG_TAG, "Auth: NO SESSION FOUND!!!");
            }
        }
        Context context = application.getApplicationContext();
        requestParams.put(REQUEST_KEY_CONNECTION_KEY, REQUEST_VALUE_CONNECTION_KEY);
        requestParams.put(REQUEST_KEY_DEVICE_IDENTIFIER, getDeviceId(context));
        requestParams.put(REQUEST_KEY_LOCALIZATION_CODE, getLocalizationCode(context));
        requestParams.put(REQUEST_KEY_MOBILE_APP_CODE, REQUEST_VALUE_MOBILE_APP_CODE);
        requestParams.put(REQUEST_KEY_MOBILE_APP_VERSION, getAppVersionForRequest(context));
    }

    /**
     * Get the app's current session
     *
     * @param application
     * @return The app's current session
     */
    private Session getSession(Application application) {
        SessionRepository sessionRepository = new SessionRepository(application);
        return sessionRepository.getCurrentSession();
    }

    /**
     * Gets the device ID (more accurately the ID of the current installation)
     *
     * @param context
     * @return The device ID
     */
    private String getDeviceId(Context context) {
        return SharedPreferencesUtil.getDeviceId(context);
    }

    /**
     * The the localization code for the device
     *
     * @param context
     * @return The localization code
     */
    private String getLocalizationCode(Context context) {
        return DeviceUtil.getLocalizationCode();
    }

    /**
     * Gets the app version formatted in the way that the API requires
     *
     * @param context
     * @return
     */
    private String getAppVersionForRequest(Context context) {
        return DeviceUtil.getShortenedAppVersion(context);
    }

    protected void cacheRequest(Application application, String objectid, String requestAction, JSONObject requestParams) {
        CachedRequestRepository cachedRequestRepository = new CachedRequestRepository(application);
        CachedRequest cachedRequest = cachedRequestRepository.getByObjectIdAndRequestAction(objectid, requestAction);
        if (cachedRequest == null) {
            cachedRequest = new CachedRequest(objectid, requestAction, requestParams.toString());
            cachedRequestRepository.insert(cachedRequest);
        } else {
            cachedRequest.setRequestAction(requestAction);
            cachedRequest.setRequestParams(requestParams.toString());
            cachedRequestRepository.update(cachedRequest);
        }
    }

    protected void cancelRequestsByURL(Context context, String url) {
        VolleyRequestQueue.getInstance(context).cancelRequestsByUrl(url);
    }

    protected JSONObject sendRequest(Context context, String url, JSONObject params) throws Exception {
//        Log.i(LOG_TAG, "Sending request to: " + url);
//        Log.i(LOG_TAG, "Request params: " + params);

        if (!NetworkUtil.hasInternetConnection(context)) {
            Log.w(LOG_TAG, "No internet connection");
            return null;
        } else {
            RequestFuture requestFuture = RequestFuture.newFuture();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, requestFuture, requestFuture);

            jsonObjectRequest.setShouldCache(false);

            // set retry policy for timing out
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // add request to queue
            VolleyRequestQueue.getInstance(context).addToRequestQueue(context, jsonObjectRequest);

            try {
                JSONObject responseJson = (JSONObject) requestFuture.get(30L, TimeUnit.SECONDS);
//                Log.i(LOG_TAG, responseJson.toString());

                // check for error message
                String result = responseJson.getString(RESPONSE_KEY_RESULT);
                if (result.equals(RESPONSE_VALUE_RESULT_ERROR)) {
                    // handle error response
                    String errorMessage = responseJson.getString(RESPONSE_KEY_ERROR_MESSAGE);
                    Log.e(LOG_TAG, "Error response returned: " + errorMessage);
                    if (errorMessage.equals(RESPONSE_VALUE_RESULT_ERROR_LOGIN_REQUIRED)) {
                        Log.e(LOG_TAG, "'Login required' returned for request to URL " + url + " with parameters " + params);
                        BroadcastUtil.sendErrorLogoutBroadcast(context, errorMessage);
                    }
                } else if (result.equals(RESPONSE_VALUE_RESULT_FORCE_UPDATE)) {
                    // handle update message
                    String updateMessage = responseJson.getString(RESPONSE_KEY_UPDATE_CONTENT);
                    Log.w(LOG_TAG, "Forced update returned: " + updateMessage);
                    BroadcastUtil.sendForceUpdateBroadcast(context, updateMessage);
                }
                return responseJson;
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Volley InterruptedException: Thrown when a waiting thread is activate before the " +
                        "condition it was waiting for has been satisfied: " + e.getMessage(), e);
                throw e;
            } catch (ExecutionException e) {
                Log.e(LOG_TAG, "Volley Execution: Thrown when attempting to retrieve the result of a task that " +
                        "aborted by throwing an exception. Message: " + e.getMessage() + " Cause: " + e.getCause(), e);
                BroadcastUtil.sendErrorMessageBroadcast(context, context.getString(R.string.error_message_internet_timeout), url);
                throw e;
            } catch (TimeoutException e) {
                Log.e(LOG_TAG, "Volley TimeoutException: " + e.getMessage(), e);
                BroadcastUtil.sendErrorMessageBroadcast(context, context.getString(R.string.error_message_internet_timeout), url);
                throw e;
            } finally {
                VolleyRequestQueue.getInstance(context).removeRequestFromQueue(jsonObjectRequest);
            }
        }
    }

    protected void sendRequest(Context context, JsonObjectRequest jsonObjectRequest) {
        Log.i(LOG_TAG, "Sending request to: " + jsonObjectRequest);
        Log.i(LOG_TAG, "Request params: " + getReadableRequestBody(jsonObjectRequest.getBody()));

        if (!NetworkUtil.hasInternetConnection(context)) {
            Log.w(LOG_TAG, "No internet connection!");
        } else {
            jsonObjectRequest.setShouldCache(false);

            // set retry policy for timing out
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // add request to queue
            VolleyRequestQueue.getInstance(context.getApplicationContext()).addToRequestQueue(context, jsonObjectRequest);
        }
    }

    protected JSONObject sendRequestWithImage(Context context, String urlString, Map<String, String> params,
                                              String imagePath) {
//        Log.i(LOG_TAG, "Sending request with image to: " + urlString);
//        Log.i(LOG_TAG, "Request params: " + params.toString());
//        Log.i(LOG_TAG, "Image to post: " + imagePath);

        if ((!NetworkUtil.hasInternetConnection(context))) {
            Log.w(LOG_TAG, "No internet connection.");
            BroadcastUtil.sendNoInternetBroadcast(context);
        } else {
            HttpURLConnection connection = null;
            DataOutputStream dataOutputStream = null;

            File imageFile = new File(imagePath);
//            Log.i(LOG_TAG, "Image size: " + imageFile.length());
            if (!imageFile.isFile()) {
                Log.e(LOG_TAG, "Image file not available");
            } else {
                try {
                    // Open URL connection
                    FileInputStream fileInputStream = new FileInputStream(imageFile);
                    URL url = new URL(urlString);

                    // Open HTTP connection
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + requestBoundary);
                    connection.setRequestProperty("uploaded_file", imageFile.getName());

                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    for (String key : params.keySet()) {
                        dataOutputStream.writeBytes(twoHyphens + requestBoundary + lineEnd);
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + key +"\"" + lineEnd);
                        dataOutputStream.writeBytes(lineEnd);
                        dataOutputStream.writeBytes(params.get(key));
                        dataOutputStream.writeBytes(lineEnd);
                    }
                    dataOutputStream.writeBytes(twoHyphens + requestBoundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"image_file\";filename=\"" + imageFile.getName() +  "\"" + lineEnd);
                    dataOutputStream.writeBytes("Content-Type: " + FileUtil.getMimeType(imageFile) + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    int bytesAvilable = fileInputStream.available();

                    int bufferSize = Math.min(bytesAvilable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvilable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvilable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + requestBoundary + twoHyphens);

                    int responseCode = connection.getResponseCode();
                    String responseMessage = connection.getResponseMessage();

//                    Log.i(LOG_TAG, "Upload message response: " + responseCode + " - " + responseMessage);
                    String response = "";
                    if (responseCode == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        response = stringBuilder.toString();
                    }

                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    return new JSONObject(response);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "There was a problem sending request with image.", e);
                }
            }
        }

        return null;
    }

    protected String buildActionUrl(Context context, String action) {
        return getCorewareApiUrl(context) + "?action=" + action;
    }

    protected String buildFileUrl(Context context, String fileId) {
        return getCorewareFileUrl(context) + "?id=" + fileId;
    }

    protected String buildImageUrl(Context context, String imageId) {
        return getCorewareImageUrl(context) + "?id=" + imageId;
    }

    private String getReadableRequestBody(byte[] requestBody) {
        try {
            return new String(requestBody, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Problem decoding request body: " + e.getLocalizedMessage(), e);
            return null;
        }
    }

    private String getCorewareApiUrl(Context context) {
        return getBaseUrl(context) + CorewareApiURL.Path.API;
    }

    private String getCorewareFileUrl(Context context) {
        return getBaseUrl(context) + CorewareApiURL.Path.FILE;
    }

    private String getCorewareImageUrl(Context context) {
        return getBaseUrl(context) + CorewareApiURL.Path.IMAGE;
    }

    private String getBaseUrl(Context context) {
        boolean inTestingMode = SharedPreferencesUtil.isAppInTestingMode(context);
        if (inTestingMode) {
            return CorewareApiURL.BASE_TEST;
        } else {
            return CorewareApiURL.BASE_PRODUCTION;
        }
    }

}
