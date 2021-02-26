package com.coreware.coreshipdriver.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.coreware.coreshipdriver.R;
import com.coreware.coreshipdriver.ui.viewmodels.SprintListViewModel;

public class SprintListFragment extends Fragment {

    private SprintListViewModel sprintListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        sprintListViewModel =
                new ViewModelProvider(this).get(SprintListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sprint_list, container, false);
        final TextView textView = root.findViewById(R.id.text_sprint_list);
        sprintListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}