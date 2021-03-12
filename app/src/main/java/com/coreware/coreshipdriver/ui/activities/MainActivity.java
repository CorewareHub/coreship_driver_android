package com.coreware.coreshipdriver.ui.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.api.services.AuthenticationApiIntentService;
import com.coreware.coreshipdriver.db.entities.User;
import com.coreware.coreshipdriver.repositories.SessionRepository;
import com.coreware.coreshipdriver.repositories.UserRepository;
import com.coreware.coreshipdriver.ui.viewmodels.UserViewModel;
import com.coreware.coreshipdriver.util.BroadcastUtil;
import com.coreware.coreshipdriver.util.IntentLaunchUtil;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    private AppBarConfiguration mAppBarConfiguration;

    private TextView mProfileUserFullName;

    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        mProfileUserFullName = headerView.findViewById(R.id.menu_profile_name);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_sprint_list, R.id.nav_edit_sprints,
                R.id.nav_incident_report, R.id.nav_profile, R.id.nav_badge,
                R.id.nav_express_pickup,R.id.nav_settings,
                R.id.nav_about, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initViewModels();

        monitorNetworkStatusChanges();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void logout() {
        // Logout of the server
        AuthenticationApiIntentService.startActionLogout(getApplicationContext());

        // Delete all session and users
        SessionRepository sessionRepository = new SessionRepository(getApplication());
        UserRepository userRepository = new UserRepository(getApplication());
        userRepository.deleteAll();
        sessionRepository.deleteAll();

        // Navigate to the Login view
        IntentLaunchUtil.launchLoginActivity(this);
        finish();
    }


    /* View Model change handlers */

    private void handleCurrentUserUpdated(User currentUser) {
        mCurrentUser = currentUser;
        mProfileUserFullName.setText(mCurrentUser.getFullName());
    }


    /* Private methods */

    private void monitorNetworkStatusChanges() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
//                Log.i(LOG_TAG, "Network connection available");
                BroadcastUtil.sendNetworkStatusChangedBroadcast(getApplicationContext());
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.w(LOG_TAG, "Network connection lost.");
                BroadcastUtil.sendNetworkStatusChangedBroadcast(getApplicationContext());
            }
        });
    }

    private void initViewModels() {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User currentUser) {
                handleCurrentUserUpdated(currentUser);
            }
        });
    }

}