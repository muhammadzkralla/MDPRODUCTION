package com.dimits.mahalladelivery.eventbus;

import com.dimits.mahalladelivery.model.PopluarCategoryModel;

public class PopluarCategoryClick {

    private PopluarCategoryModel popluarCategoryModel;

    public PopluarCategoryClick(PopluarCategoryModel popluarCategoryModel) {
        this.popluarCategoryModel = popluarCategoryModel;
    }

    public PopluarCategoryModel getPopluarCategoryModel() {
        return popluarCategoryModel;
    }

    public void setPopluarCategoryModel(PopluarCategoryModel popluarCategoryModel) {
        this.popluarCategoryModel = popluarCategoryModel;
    }
}
