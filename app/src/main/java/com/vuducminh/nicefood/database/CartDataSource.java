package com.vuducminh.nicefood.database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String uid,String restaurantId);


    Single<Integer> countItemInCart(String uid,String restaurantId);

    Single<Double> sumPriceInCart(String uid,String restaurantId);

    Single<CartItem> getItemInCart(String foodId,String uid,String restaurantId);

    Completable insertOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCartItem(CartItem cartItems);

    Single<Integer> deleteCartItem(CartItem cartItems);

    Single<Integer> cleanCart(String uid,String restaurantId);

    Single<CartItem> getItemAllOptionsInCart(String uid,String categoryId,String foodId,String foodSize,String foodAddon,String restaurantId);

}
