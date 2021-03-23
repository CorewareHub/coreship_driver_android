package com.coreware.coreshipdriver.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.api.services.DashboardApiIntentService;
import com.coreware.coreshipdriver.db.entities.User;
import com.coreware.coreshipdriver.ui.activities.MainActivity;
import com.coreware.coreshipdriver.ui.viewmodels.UserViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class DashboardFragment extends Fragment {
    private static final String LOG_TAG = DashboardFragment.class.getName();

    private Button mClockInButton;
    private Button mClockOutButton;
    private ProgressBar mProgressSpinner;
    private View mSprintsButtonContainer;

    private User mCurrentUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModels();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mClockInButton = rootView.findViewById(R.id.dashboard_clock_in_button);
        mClockInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressSpinner(true);
                DashboardApiIntentService.startActionClockIn(getContext());
            }
        });

        mClockOutButton = rootView.findViewById(R.id.dashboard_clock_out_button);
        mClockOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressSpinner(true);
                DashboardApiIntentService.startActionClockOut(getContext());
            }
        });

        mProgressSpinner = rootView.findViewById(R.id.dashboard_clock_in_out_progress);

        Button expressPickupButton = rootView.findViewById(R.id.dashboard_express_pickup_button);
        expressPickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.navigateToExpressPickup();
                }
            }
        });

        mSprintsButtonContainer = rootView.findViewById(R.id.dashboard_sprints_button_container);

        Button sprintsButton = rootView.findViewById(R.id.dashboard_sprints_button);

        updateView();

        return rootView;
    }


    /* View Model change handlers */

    private void handleCurrentUserUpdated(User currentUser) {
        mCurrentUser = currentUser;
        showProgressSpinner(false);
        updateView();
    }


    /* Private methods */

    private void updateView() {
        if (mCurrentUser != null) {
            if (mCurrentUser.isClockedIn()) {
                mClockInButton.setVisibility(View.GONE);
                mClockOutButton.setVisibility(View.VISIBLE);
                mSprintsButtonContainer.setVisibility(View.VISIBLE);
            } else {
                mClockInButton.setVisibility(View.VISIBLE);
                mClockOutButton.setVisibility(View.GONE);
                mSprintsButtonContainer.setVisibility(View.GONE);
            }
        }
    }

    private void showProgressSpinner(boolean showSpinner) {
        if (showSpinner) {
            mClockInButton.setVisibility(View.GONE);
            mClockOutButton.setVisibility(View.GONE);
            mProgressSpinner.setVisibility(View.VISIBLE);
        } else {
            mProgressSpinner.setVisibility(View.GONE);
            if (mCurrentUser!= null && mCurrentUser.isClockedIn()) {
                mClockOutButton.setVisibility(View.VISIBLE);
            } else {
                mClockInButton.setVisibility(View.VISIBLE);
            }
        }
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