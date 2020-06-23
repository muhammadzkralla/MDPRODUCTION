package com.dimits.mahalladelivery.callback;

import com.dimits.mahalladelivery.model.BestDealModel;

import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> BestDealModels);
    void onBestDealLoadFailed(String message);
}
