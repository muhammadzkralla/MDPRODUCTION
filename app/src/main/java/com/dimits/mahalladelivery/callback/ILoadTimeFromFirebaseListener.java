package com.dimits.mahalladelivery.callback;

import com.dimits.mahalladelivery.model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel orderModel, long estimateTimeInMs);
    void onLoadtimeFailed(String message);
}
