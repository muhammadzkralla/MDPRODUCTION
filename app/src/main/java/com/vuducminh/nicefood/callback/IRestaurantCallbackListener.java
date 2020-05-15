package com.vuducminh.nicefood.callback;

import com.vuducminh.nicefood.model.CategoryModel;
import com.vuducminh.nicefood.model.RestaurantModel;

import java.util.List;

public interface IRestaurantCallbackListener {
    void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList);
    void onRestaurantLoadFailed(String message);
}
