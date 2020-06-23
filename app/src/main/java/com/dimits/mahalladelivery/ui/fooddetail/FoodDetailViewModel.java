package com.dimits.mahalladelivery.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dimits.mahalladelivery.common.Common;
import com.dimits.mahalladelivery.model.CommentModel;
import com.dimits.mahalladelivery.model.FoodModel;

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