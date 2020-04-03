package com.vuducminh.nicefood.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.model.CommentModel;
import com.vuducminh.nicefood.model.FoodModel;

public class FoodDetailViewModel extends ViewModel {

    private MutableLiveData<FoodModel> modelMutableLiveDataFoodModel;
    private MutableLiveData<CommentModel> modelMutableLiveDataCommentModel;


    public FoodDetailViewModel() {
        modelMutableLiveDataCommentModel = new MutableLiveData<>();
    }

    public MutableLiveData<CommentModel> getModelMutableLiveDataCommentModel() {
        return modelMutableLiveDataCommentModel;
    }

    public void setModelMutableLiveDataCommentModel(CommentModel commentModel) {
        if(modelMutableLiveDataCommentModel != null) {
            modelMutableLiveDataCommentModel.setValue(commentModel);
        }
    }

    public MutableLiveData<FoodModel> getModelMutableLiveDataFoodModel() {
        if(modelMutableLiveDataFoodModel == null) {
            modelMutableLiveDataFoodModel = new MutableLiveData<>();
        }
        modelMutableLiveDataFoodModel.setValue(Common.selectedFood);
        return modelMutableLiveDataFoodModel;
    }

    public void setFoodModel(FoodModel foodModel) {
        if(modelMutableLiveDataFoodModel != null) {
            modelMutableLiveDataFoodModel.setValue(foodModel);
        }
    }
}