package com.coreware.coreshipdriver.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.api.services.AuthenticationApiIntentService;
import com.coreware.coreshipdriver.util.BroadcastUtil;
import com.coreware.coreshipdriver.util.IntentLaunchUtil;
import com.coreware.coreshipdriver.util.NetworkUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoginActivity.class.getName();

    private TextView mErrorMessageTextView;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private View mProgressContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerForBroadcasts();

        initViews();
    }


    /* Broadcast Receivers */

    private BroadcastReceiver authenticationCompleteBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Authentication complete broadcast received");
            showProgressIndicator(false);

            IntentLaunchUtil.launchMainActivity(LoginActivity.this);
            finish();
        }
    };

    private BroadcastReceiver errorMessageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i(LOG_TAG, "Error message broadcast received");
            showProgressIndicator(false);

            String errorMessage = intent.getStringExtra(BroadcastUtil.BROADCAST_KEY_ERROR_MESSAGE);
            showErrorMessage(errorMessage);
        }
    };


    /* Private methods */

    /**
     * Attempts to sign in to account. If there are are errors, no login attempt is made.
     */
    private void attemptLogin() {
        if (!NetworkUtil.hasInternetConnection(getApplicationContext())) {
            showErrorMessage("No network connection available. Please connection your device to the internet.");
            return;
        }

        // reset errors
        mErrorMessageTextView.setVisibility(View.GONE);
        mErrorMessageTextView.setText("");

        // get input values
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // input validation
        if (username == null || username.trim().length() == 0) {
            showErrorMessage("Please enter a username and password");
            return;
        }
        if (password == null || password.trim().length() == 0) {
            showErrorMessage("Please enter a username and password");
            return;
        }

        performLogin(username.trim(), password.trim());
    }

    private void performLogin(String username, String password) {
        showProgressIndicator(true);
        AuthenticationApiIntentService.startActionLogin(getApplicationContext(), username, password);
    }

    private void showErrorMessage(String errorMessage) {
        mErrorMessageTextView.setText(errorMessage);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showProgressIndicator(boolean showProgressIndicator) {
        if (showProgressIndicator) {
            mUsernameEditText.setEnabled(false);
            mPasswordEditText.setEnabled(false);
            mLoginButton.setVisibility(View.GONE);
            mProgressContainer.setVisibility(View.VISIBLE);
        } else {
            mUsernameEditText.setEnabled(true);
            mPasswordEditText.setEnabled(true);
            mLoginButton.setVisibility(View.VISIBLE);
            mProgressContainer.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        mErrorMessageTextView = findViewById(R.id.login_error_message);
        mErrorMessageTextView.setVisibility(View.GONE);

        mUsernameEditText = findViewById(R.id.login_username_field);
        mPasswordEditText = findViewById(R.id.login_password_field);

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mProgressContainer = findViewById(R.id.login_progress_indicator_container);

        showProgressIndicator(false);
    }

    private void registerForBroadcasts() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(authenticationCompleteBroadcastReceiver, new IntentFilter(BroadcastUtil.ACTION_AUTHENTICATION_COMPLETE));
        localBroadcastManager.registerReceiver(errorMessageBroadcastReceiver, new IntentFilter(BroadcastUtil.ACTION_ERROR_MESSAGE));
    }

    private void unregisterForBroadcasts() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(authenticationCompleteBroadcastReceiver);
        localBroadcastManager.unregisterReceiver(errorMessageBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        unregisterForBroadcasts();
        super.onDestroy();
    }

}