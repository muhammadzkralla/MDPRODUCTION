package com.dimits.mahalladelivery.database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String uid,String restaurantId) {
        return cartDAO.getAllCart(uid,restaurantId);
    }

    @Override
    public Single<Integer> countItemInCart(String uid,String restaurantId) {
        return cartDAO.countItemInCart(uid,restaurantId);
    }

    @Override
    public Single<Double> sumPriceInCart(String uid,String restaurantId) {
        return cartDAO.sumPriceInCart(uid,restaurantId);
    }

    @Override
    public Single<CartItem> getItemInCart(String foodId, String uid,String restaurantId) {
        return cartDAO.getItemInCart(foodId,uid,restaurantId);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem... cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCartItem(CartItem cartItems) {
        return cartDAO.updateCartItem(cartItems);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cartItems) {
        return cartDAO.deleteCartItem(cartItems);
    }

    @Override
    public Single<Integer> cleanCart(String uid,String restaurantId) {
        return cartDAO.cleanCart(uid,restaurantId);
    }

    @Override
    public Single<CartItem> getItemAllOptionsInCart(String uid, String categoryId, String foodId, String foodSize, String foodAddon, String restaurantId) {
        return cartDAO.getItemAllOptionsInCart(uid,categoryId,foodId,foodSize,foodAddon,restaurantId);
    }
}
