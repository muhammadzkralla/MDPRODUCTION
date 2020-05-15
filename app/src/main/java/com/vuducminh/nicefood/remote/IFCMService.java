package com.vuducminh.nicefood.remote;

import com.vuducminh.nicefood.model.FCMservice.FCMResponse;
import com.vuducminh.nicefood.model.FCMservice.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Conten-Type:application/json",
            "Authorization:key=AAAAGPqmrD8:APA91bHNV3n0SPHl7oY2eiknHmWBAwA5CyQOmcZyjzDHQ6BltiV8nYmMXjbLQp9wKwTma3nk-ZZZsX0OlngR1ML0e4PU134oDxekY-nxZoFk_m-046bT-QlPELnZBlDMUbPqA7JE1Fmp"
    })
    @POST("fom/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
