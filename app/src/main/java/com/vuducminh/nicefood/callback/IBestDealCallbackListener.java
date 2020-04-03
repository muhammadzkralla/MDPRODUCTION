package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.BestDealModel;

import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> BestDealModels);
    void onBestDealLoadFailed(String message);
}
