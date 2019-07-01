package com.trigger.snha;

import androidx.fragment.app.FragmentManager;

import com.trigger.snha.views.JokesParentFragment;
import com.trigger.snha.views.SignInFragment;

/**
 * Singleton provides method to change fragments in the main activity
 */
public class Router {
    private FragmentManager fm;

    public void setFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    public void openSignInFragment() {
        // for brevity and happy path - no checking of already added fragment

        fm.beginTransaction()
                .replace(android.R.id.content, SignInFragment.newInstance())
                .commit();
    }

    public void openJokesFragment() {
        fm.beginTransaction()
                .replace(android.R.id.content, JokesParentFragment.newInstance())
                .commit();
    }

    private static Router instance;

    private Router() {

    }

    public static Router getInstance() {
        if (instance == null) {
            instance = new Router();
        }

        return instance;
    }
}
