package com.trigger.snha.views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class JokesPagerAdapter extends FragmentStatePagerAdapter {
    private int size;

    public JokesPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return JokesFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return size;
    }

    public void setSize(int size) {
        if(this.size == size) return;

        this.size = size;
        notifyDataSetChanged();
    }
}
