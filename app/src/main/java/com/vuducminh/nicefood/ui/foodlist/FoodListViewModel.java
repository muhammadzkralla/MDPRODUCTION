package com.vuducminh.nicefood.ui.foodlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.model.FoodModel;

import java.util.List;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList;

    public FoodListViewModel() {

    }

    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList() {
        if(mutableLiveDataFoodList == null) {
            mutableLiveDataFoodList = new MutableLiveData<>();
        }
        mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());
        return mutableLiveDataFoodList;
    }
}