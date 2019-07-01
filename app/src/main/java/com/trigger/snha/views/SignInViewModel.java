package com.trigger.snha.views;

import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.trigger.snha.dto.User;
import com.trigger.snha.model.GlobalRepository;

public class SignInViewModel extends ViewModel {

    // Provides latest saved state to the subscriber
    BehaviorRelay<SignInScreenState> state;

    private String firstname;
    private String lastname;

    public SignInViewModel() {
        state = BehaviorRelay.createDefault(new SignInScreenState(SignInScreenState.Status.INIT, firstname, lastname));
    }

    public BehaviorRelay<SignInScreenState> getState() {
        return state;
    }

    public void submit() {
        User user = new User(firstname, lastname);

        GlobalRepository.getInstance().getUserRepository().signIn(user);
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname.trim();

        checkState();
    }

    public void setLastname(String lastname) {
        this.lastname = lastname.trim();

        checkState();
    }

    /**
     * Checks the fields and allows the view to click 'Submit' button
     */
    private void checkState() {
        if(TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname)) {
            state.accept(new SignInScreenState(SignInScreenState.Status.INIT, firstname, lastname));
        } else {
            state.accept(new SignInScreenState(SignInScreenState.Status.READY_TO_SUBMIT, firstname, lastname));
        }
    }

    /**
     * Class defining the state for SignInFragment
     */
    static public class SignInScreenState {
        public enum Status {
            INIT, // fragment is opened, fields are empty, button is disabled
            READY_TO_SUBMIT, // button can be clicked
            SUBMITTING // not used (in case of long waiting behaviour will be implemented)
        }

        public Status status;
        public String firstname;
        public String lastname;

        public SignInScreenState(Status status, String firstname, String lastname) {
            this.status = status;
            this.firstname = firstname;
            this.lastname = lastname;
        }
    }
}
