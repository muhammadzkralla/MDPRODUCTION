package com.dimits.mahalladelivery.callback;

import com.dimits.mahalladelivery.model.PopluarCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopluarCategoryModel> popluarCategoryModels);
    void onPopularLoadFailed(String message);
}
