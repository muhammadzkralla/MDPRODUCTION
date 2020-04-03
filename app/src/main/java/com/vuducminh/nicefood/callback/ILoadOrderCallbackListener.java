package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.OrderModel;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderLoadSuccess(List<OrderModel> corderModels);
    void onLoadOrderLoadFailed(String message);
}
