package com.trigger.snha.model;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.trigger.snha.dto.User;

public class UserRepository {

    BehaviorRelay<UserState> state;
    User user;

    public UserRepository() {
        state = BehaviorRelay.createDefault(new UserState(UserState.Status.GUEST, null));
    }

    public BehaviorRelay<UserState> getState() {
        return state;
    }

    public void signIn(User user) {
        this.user = user;

        state.accept(new UserState(UserState.Status.SIGNED_IN, this.user));
    }

    static public class UserState {
        public enum Status {
            GUEST, // initial screen
            LOADING, // not used (progress bar could be shown in case of network sign-in)
            SIGNED_IN // user is defined
        }

        public Status status;
        public User user;

        public UserState(Status status, User user) {
            this.status = status;
            this.user = user;
        }
    }
}
