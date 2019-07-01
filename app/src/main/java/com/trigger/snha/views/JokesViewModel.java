package com.trigger.snha.views;

import androidx.lifecycle.ViewModel;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.trigger.snha.helpers.Analytics;
import com.trigger.snha.model.GlobalRepository;
import com.trigger.snha.model.JokesRepository;

import io.reactivex.disposables.CompositeDisposable;

public class JokesViewModel extends ViewModel {

    private BehaviorRelay<Integer> pagesCountState;

    private final JokesRepository jokesRepository;
    private int currentPage; // current position of ViewPager
    private int pagesCount; // total number of pages in ViewPager

    private CompositeDisposable disposables = new CompositeDisposable();

    public JokesViewModel() {
        jokesRepository = GlobalRepository.getInstance().getJokesRepository();
        pagesCount = 1;
        pagesCountState = BehaviorRelay.createDefault(pagesCount);

        disposables.add(jokesRepository.getState()
                .subscribe(jokesState -> {
                    switch (jokesState.status) {
                        case CACHE_LOADING:
                            break;
                        case FETCHING:
                            break;
                        case READY:
                            if(jokesState.jokes.size() == 0 || currentPage == jokesState.jokes.size() - 1) {
                                jokesRepository.addRandomJoke();
                            }

                            if(pagesCount <= jokesState.jokes.size()) {
                                pagesCount = jokesState.jokes.size() + 1;
                                pagesCountState.accept(pagesCount);
                            }
                            break;
                        case ERROR:
                            break;
                    }
                }, err -> {
                    Analytics.lg("JVM error", err.getMessage());
                }));
    }

    public BehaviorRelay<Integer> getPagesCountState() {
        return pagesCountState;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;

        checkToFetchJoke();
    }

    public void checkToFetchJoke() {
        JokesRepository.JokesState jokesState = jokesRepository.getState().getValue();

        if(jokesState.status == JokesRepository.JokesState.Status.READY && this.currentPage == jokesState.jokes.size() - 1) {
            jokesRepository.addRandomJoke();
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        disposables.dispose();
    }
}
