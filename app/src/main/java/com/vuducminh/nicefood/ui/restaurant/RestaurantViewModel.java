package com.vuducminh.nicefood.ui.restaurant;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.callback.IRecyclerClickListener;
import com.vuducminh.nicefood.callback.IRestaurantCallbackListener;
import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.model.RestaurantModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel implements IRestaurantCallbackListener {

    private MutableLiveData<List<RestaurantModel>> restaurantListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IRestaurantCallbackListener listener;

    public RestaurantViewModel() {
        listener = this;
    }


    public MutableLiveData<List<RestaurantModel>> getRestaurantListMutable() {
        if(restaurantListMutable == null)
        {
            restaurantListMutable = new MutableLiveData<>();
            loadRestaurantFromFirebase();
        }
        return restaurantListMutable;
    }

    private void loadRestaurantFromFirebase() {
        List<RestaurantModel> restaurantModels = new ArrayList<>();
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot restaurantSnapShot:dataSnapshot.getChildren())
                    {
                        RestaurantModel restaurantModel = restaurantSnapShot.getValue(RestaurantModel.class);
                        restaurantModel.setUid(restaurantSnapShot.getKey());
                        restaurantModels.add(restaurantModel);
                    }
                    if (restaurantModels.size() > 0)
                        listener.onRestaurantLoadSuccess(restaurantModels);
                    else
                        listener.onRestaurantLoadFailed("Restaurant list empty");
                }
                else
                    listener.onRestaurantLoadFailed("Restaurant list doesn't exists");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }


    @Override
    public void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList) {
        restaurantListMutable.setValue(restaurantModelList);
    }

    @Override
    public void onRestaurantLoadFailed(String message) {
        messageError.setValue(message);
    }
}
