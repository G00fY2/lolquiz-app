package com.g00fy2.lolquiz.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by thoma on 12.10.2016.
 */

public class RetroClient {

    private static Retrofit getRetrofitInstance(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RestApi getRestApi(String baseUrl) {
        return getRetrofitInstance(baseUrl).create(RestApi.class);
    }
}
