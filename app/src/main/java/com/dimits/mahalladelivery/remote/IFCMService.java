package com.dimits.mahalladelivery.remote;

import com.dimits.mahalladelivery.model.FCMservice.FCMResponse;
import com.dimits.mahalladelivery.model.FCMservice.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAGPqmrD8:APA91bHNV3n0SPHl7oY2eiknHmWBAwA5CyQOmcZyjzDHQ6BltiV8nYmMXjbLQp9wKwTma3nk-ZZZsX0OlngR1ML0e4PU134oDxekY-nxZoFk_m-046bT-QlPELnZBlDMUbPqA7JE1Fmp"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
