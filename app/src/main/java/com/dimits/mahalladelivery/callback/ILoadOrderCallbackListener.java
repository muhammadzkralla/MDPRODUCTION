package com.dimits.mahalladelivery.callback;

import com.dimits.mahalladelivery.model.OrderModel;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderLoadSuccess(List<OrderModel> corderModels);
    void onLoadOrderLoadFailed(String message);
}
