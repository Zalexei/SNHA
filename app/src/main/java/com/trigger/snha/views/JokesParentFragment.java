package com.trigger.snha.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.trigger.snha.R;
import com.trigger.snha.helpers.Analytics;
import com.trigger.snha.helpers.Commons;
import com.trigger.snha.model.GlobalRepository;
import com.trigger.snha.model.JokesRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class JokesParentFragment extends Fragment {

    @BindView(R.id.vp_jokes)
    ViewPager vpJokes;

    private JokesViewModel mViewModel;
    JokesPagerAdapter adapter;
    private Unbinder binder;

    CompositeDisposable disposables = new CompositeDisposable();

    public static JokesParentFragment newInstance() {
        return new JokesParentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.jokes_parent_fragment, container, false);

        binder = ButterKnife.bind(this, view);

        adapter = new JokesPagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpJokes.setAdapter(adapter);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            vpJokes.setPageMargin(-Commons.dpToPx(300, getContext()));
        } else {
            vpJokes.setPageMargin(-Commons.dpToPx(80, getContext()));
        }

        vpJokes.setOffscreenPageLimit(2);
        vpJokes.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewModel.setCurrentPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(JokesViewModel.class);

        adapter.setSize(mViewModel.getPagesCountState().getValue());
        vpJokes.setCurrentItem(mViewModel.getCurrentPage());

        disposables.add(mViewModel.getPagesCountState()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(count -> {
            adapter.setSize(count);
        }, err -> {
            Analytics.lg("JPF error", err.getMessage());
        }));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binder.unbind();
        disposables.dispose();
    }
}
