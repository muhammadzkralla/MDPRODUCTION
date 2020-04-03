package com.vuducminh.nicefood.remote;

import com.vuducminh.nicefood.common.CommonAgr;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitICloudClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://us-central1-menumakanan-fbf35.cloudfunctions.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

        return instance;
    }
}
