package com.vuducminh.nicefood.remote;

import com.vuducminh.nicefood.common.CommonAgr;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFCMClient {
    private static Retrofit instance;
    public static Retrofit getInstance() {
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(CommonAgr.URL_FCM)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}
