package com.trigger.snha.network;

import com.trigger.snha.dto.DataResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JokesApi {
    @GET("jokes/random")
    Single<DataResponse> fetchRandomJoke(@Query("firstName") String firstname,
                                         @Query("lastName") String lastname,
                                         @Query("limitTo") String limitTo);
}
