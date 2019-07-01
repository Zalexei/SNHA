package com.trigger.snha.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.trigger.snha.dto.DataResponse;
import com.trigger.snha.dto.User;
import com.trigger.snha.model.GlobalRepository;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JokesService {
    private static final String BASE_URL = "https://api.icndb.com";

    private static final JokesService ourInstance = new JokesService();

    private JokesApi api;

    public static JokesService getInstance() {
        return ourInstance;
    }

    private void setInterface() {
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        api = retrofit.create(JokesApi.class);
    }

    private JokesService() {
        setInterface();
    }

    public Single<DataResponse> getRandomJoke() {
        User user = GlobalRepository.getInstance().getUserRepository().getState().getValue().user;

        return api.fetchRandomJoke(user.getFirstname(), user.getLastname(), "[nerdy]").subscribeOn(Schedulers.io());
    }
}
