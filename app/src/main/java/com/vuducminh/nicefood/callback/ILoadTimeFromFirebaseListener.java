package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel orderModel, long estimateTimeInMs);
    void onLoadtimeFailed(String message);
}
