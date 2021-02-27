package com.coreware.coreshipdriver.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coreware.coreshipdriver.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SprintListFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sprint_list, container, false);
        return root;
    }
}