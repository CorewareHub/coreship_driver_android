package com.coreware.coreshipdriver.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SprintListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SprintListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the sprint list view");
    }

    public LiveData<String> getText() {
        return mText;
    }
}