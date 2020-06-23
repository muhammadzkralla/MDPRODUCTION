package com.dimits.mahalladelivery.callback;

import com.dimits.mahalladelivery.model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> CategoryModels);
    void onCategoryLoadFailed(String message);
}
