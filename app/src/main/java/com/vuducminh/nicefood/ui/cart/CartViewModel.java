package com.vuducminh.nicefood.ui.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.database.CartDataSource;
import com.vuducminh.nicefood.database.CartDatabase;
import com.vuducminh.nicefood.database.CartItem;
import com.vuducminh.nicefood.database.LocalCartDataSource;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItem;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }
    public void onStop() {
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataItem() {
        if(mutableLiveDataCartItem == null) {
            mutableLiveDataCartItem = new MutableLiveData<>();
        }
        getAllCartItem();
        return mutableLiveDataCartItem;
    }

    private void getAllCartItem() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<CartItem>>() {
            @Override
            public void accept(List<CartItem> cartItems) throws Exception {
                mutableLiveDataCartItem.setValue(cartItems);
            }
        },throwable -> {
            mutableLiveDataCartItem.setValue(null);
        }));
    }
}