package com.vuducminh.nicefood.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.callback.IBestDealCallbackListener;
import com.vuducminh.nicefood.callback.IPopularCallbackListener;
import com.vuducminh.nicefood.common.CommonAgr;
import com.vuducminh.nicefood.model.BestDealModel;
import com.vuducminh.nicefood.model.PopluarCategoryModel;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, IBestDealCallbackListener {

   private MutableLiveData<List<PopluarCategoryModel>> popularList;
   private MutableLiveData<List<BestDealModel>> bestDealList;
   private MutableLiveData<String> messageError;
   private IPopularCallbackListener popularCallbackListener;
   private IBestDealCallbackListener bestDealCallbackListener;

    public HomeViewModel() {
       popularCallbackListener = this;
       bestDealCallbackListener = this;
    }

    public MutableLiveData<List<PopluarCategoryModel>> getPopularList() {
        if(popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList();
        }
        return popularList;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealList() {
        if(bestDealList == null) {
            bestDealList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBestDealList();
        }
        return bestDealList;
    }

    private void loadBestDealList() {
        List<BestDealModel> tempList = new ArrayList<>();
        DatabaseReference bestDealRef = FirebaseDatabase.getInstance().getReference(CommonAgr.BEST_DEAL_REF);
        bestDealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren()) {
                    BestDealModel model = itemSnapShot.getValue(BestDealModel.class);
                    tempList.add(model);
                }
                bestDealCallbackListener.onBestDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bestDealCallbackListener.onBestDealLoadFailed(databaseError.getMessage());
            }
        });
    }

    private void loadPopularList() {
        List<PopluarCategoryModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(CommonAgr.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren()) {
                    PopluarCategoryModel model = itemSnapShot.getValue(PopluarCategoryModel.class);
                    tempList.add(model);
                }
                popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopluarCategoryModel> popluarCategoryModels) {
        popularList.setValue(popluarCategoryModels);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }

    @Override
    public void onBestDealLoadSuccess(List<BestDealModel> BestDealModels) {
        bestDealList.setValue(BestDealModels);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
        messageError.setValue(message);
    }
}