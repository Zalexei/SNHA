package com.trigger.snha.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.trigger.snha.R;
import com.trigger.snha.Router;
import com.trigger.snha.helpers.Analytics;
import com.trigger.snha.model.GlobalRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Application is 'one-activity-app'
 */
public class MainActivity extends AppCompatActivity {

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiates router helper with fragment manager
        Router.getInstance().setFragmentManager(getSupportFragmentManager());

        // Subscribing to user state to understand what fragment to show
        // In case of sign-in, sign-out fragment will be changed immediately
        disposables.add(GlobalRepository.getInstance().getUserRepository().getState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userState -> {
                   switch (userState.status) {
                        case GUEST:
                            Router.getInstance().openSignInFragment();
                            break;
                        case SIGNED_IN:
                            Router.getInstance().openJokesFragment();
                            break;
                    }

                }, err -> {
                    Analytics.lg("MA err", err.getMessage());
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposables.dispose();
    }
}
