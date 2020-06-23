package com.dimits.mahalladelivery.remote;

import com.dimits.mahalladelivery.model.BraintreeToken;
import com.dimits.mahalladelivery.model.BraintreeTransaction;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ICloudFunction {
    @GET("token")
    Observable<BraintreeToken> getToken();

    @POST("checkout")
    @FormUrlEncoded
    Observable<BraintreeTransaction> submitPayment(@Field("amount") double amount,
                                                   @Field("payment_method_nonce") String nonce);
}
