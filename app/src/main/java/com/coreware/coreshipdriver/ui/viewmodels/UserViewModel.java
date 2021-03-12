package com.coreware.coreshipdriver.ui.viewmodels;

import android.app.Application;

import com.coreware.coreshipdriver.db.entities.User;
import com.coreware.coreshipdriver.repositories.UserRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Code based on https://developer.android.com/codelabs/android-room-with-a-view#8
 */

public class UserViewModel extends AndroidViewModel {

    private UserRepository repository;
    private LiveData<User> currentUser;


    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);

    }

    public LiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
            loadCurrentUser();
        }
        return currentUser;
    }


    /* Private methods */

    private void loadCurrentUser() {
        currentUser = repository.getLiveDataCurrentUser();
    }

}
