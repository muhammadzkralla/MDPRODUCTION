package com.vuducminh.nicefood.ui.view_orders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vuducminh.nicefood.model.OrderModel;

import java.util.List;

public class ViewOrdersViewModel extends ViewModel {

    private MutableLiveData<List<OrderModel>> mutableLiveDataOrderList;

    public ViewOrdersViewModel() {
        mutableLiveDataOrderList = new MutableLiveData<>();
    }

    public MutableLiveData<List<OrderModel>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<OrderModel> orderModelList) {
        this.mutableLiveDataOrderList.setValue(orderModelList);
    }
}