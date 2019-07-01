package com.trigger.snha.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.trigger.snha.R;
import com.trigger.snha.helpers.Analytics;
import com.trigger.snha.model.GlobalRepository;
import com.trigger.snha.model.JokesRepository;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class JokesFragment extends Fragment {

    private static final String JOKE_POSITION = "joke_position";

    @BindView(R.id.tv_joke)
    TextView tvJoke;

    @BindView(R.id.pb_joke)
    ProgressBar pbJoke;

    @BindView(R.id.b_joke_retry)
    Button bRetry;

    CompositeDisposable disposables = new CompositeDisposable();

    private int jokePosition = -1; // position of the fragment in adapter
    private Unbinder binder;

    public static JokesFragment newInstance(int position) {
        final JokesFragment jokesFragment = new JokesFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(JOKE_POSITION, position);
        jokesFragment.setArguments(bundle);

        return jokesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            jokePosition = getArguments().getInt(JOKE_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.jokes_fragment, container, false);

        binder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        disposables.add(GlobalRepository.getInstance().getJokesRepository().getState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jokesState -> {
                    // checking that fragment wasn't destroyed
                    if(getContext() == null) return;

                    // case for recreated fragments when joke is already fetched
                    if(jokePosition < jokesState.jokes.size()) {
                        showCompletedState(jokesState);
                        return;
                    }

                    // case for last page of viewpager
                    switch (jokesState.status) {
                        case CACHE_LOADING:
                        case FETCHING:
                            showLoadingState();
                            break;
                        case ERROR:
                            showErrorState();
                            break;
                        case READY:
                            // handled in the beginning
                            break;
                    }
                }, err -> {
                    Analytics.lg("JF error", err.getMessage());
                }));
    }

    private void showErrorState() {
        bRetry.setVisibility(View.VISIBLE);
        tvJoke.setVisibility(View.GONE);
        pbJoke.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        tvJoke.setVisibility(View.GONE);
        pbJoke.setVisibility(View.VISIBLE);
        bRetry.setVisibility(View.GONE);
    }

    private void showCompletedState(JokesRepository.JokesState jokesState) {
        bRetry.setVisibility(View.GONE);
        pbJoke.setVisibility(View.GONE);
        tvJoke.setVisibility(View.VISIBLE);
        String joke = jokesState.jokes.get(jokePosition).getJoke();
        joke = joke.replace("&quot;", "\"");

        tvJoke.setText(joke);

        // after setting the joke unsubscribing from observable
        disposables.dispose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binder.unbind();
    }

    // Butterknife click handler
    @OnClick(R.id.b_joke_retry)
    public void onRetryClicked() {
        final JokesRepository jokesRepository = GlobalRepository.getInstance().getJokesRepository();
        jokesRepository.addRandomJoke();
    }
}
