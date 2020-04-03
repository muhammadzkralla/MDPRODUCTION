package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> CategoryModels);
    void onCategoryLoadFailed(String message);
}
