package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.PopluarCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopluarCategoryModel> popluarCategoryModels);
    void onPopularLoadFailed(String message);
}
