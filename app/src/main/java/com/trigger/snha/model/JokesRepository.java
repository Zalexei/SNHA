package com.trigger.snha.model;

import android.annotation.SuppressLint;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.trigger.snha.dto.Joke;
import com.trigger.snha.helpers.Analytics;
import com.trigger.snha.network.JokesService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import nl.nl2312.rxcupboard2.RxCupboard;
import nl.nl2312.rxcupboard2.RxDatabase;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class JokesRepository {
    BehaviorRelay<JokesState> state;

    List<Joke> jokes = new ArrayList<>();

    private boolean pendingFetching;

    @SuppressLint("CheckResult")
    public JokesRepository() {
        state = BehaviorRelay.create();

        RxDatabase rxDatabase = RxCupboard.with(cupboard(), GlobalRepository.getInstance().dbHelper.getReadableDatabase());
        rxDatabase
                .query(Joke.class)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(s -> state.accept(new JokesState(JokesState.Status.CACHE_LOADING, jokes)))
                .toList()
                .subscribe(jokes -> {
                    this.jokes = jokes;

                    state.accept(new JokesState(JokesState.Status.READY, this.jokes));
                }, err -> {
                    Analytics.lg("JR error", err.getMessage());
                }
        );
    }

    public BehaviorRelay<JokesState> getState() {
        return state;
    }

    @SuppressLint("CheckResult")
    public void addRandomJoke() {
        if(state.getValue().status == JokesState.Status.FETCHING){
            return;
        }

        // Joke fetching with updating all the states (fetching, fetched, error)
        // Delay for 2 seconds added in order to show loading state
        JokesService.getInstance().getRandomJoke()
                .delay(2, TimeUnit.SECONDS)
                .doOnSubscribe(s -> state.accept(new JokesState(JokesState.Status.FETCHING, jokes)))
                .doOnError(err -> state.accept(new JokesState(JokesState.Status.ERROR, jokes, err)))
                .subscribe(jokeObject -> {
                    Joke joke = new Joke(jokes.size(), jokeObject.getValue().getJoke());
                    jokes.add(joke);

                    state.accept(new JokesState(JokesState.Status.READY, jokes));

                    RxDatabase rxDatabase = RxCupboard.with(cupboard(), GlobalRepository.getInstance().dbHelper.getWritableDatabase());
                    rxDatabase.put(joke)
                            .subscribeOn(Schedulers.io())
                            .subscribe(result -> {
//                                if(pendingFetching) {
//                                    pendingFetching = false;
//                                    addRandomJoke();
//                                }
                            }, err -> {
                                Analytics.lg("JR saving joke error", err.getMessage());
                            }
                    );
                }, err -> {
                    Analytics.lg("JR addRandomJoke error", err.getMessage());
                });
    }

    static public class JokesState {
        public enum Status {
            CACHE_LOADING,
            FETCHING,
            READY,
            ERROR
        }

        public Status status;
        public List<Joke> jokes;
        public Throwable error;

        JokesState(Status status, List<Joke> jokes) {
            this.status = status;
            this.jokes = jokes;
        }

        JokesState(Status status, List<Joke> jokes, Throwable error) {
            this.status = status;
            this.jokes = jokes;
            this.error = error;
        }
    }
}
