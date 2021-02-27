package com.coreware.coreshipdriver.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.util.IntentLaunchUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentLaunchUtil.launchMainActivity(LoginActivity.this);
                finish();
            }
        });
    }
}