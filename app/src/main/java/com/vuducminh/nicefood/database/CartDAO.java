package com.vuducminh.nicefood.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

//Cart Data Access Oject
@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart WhERE uid=:uid")
    Flowable<List<CartItem>> getAllCart(String uid);

    @Query("SELECT SUM(foodQuantity) FROM Cart WHERE uid=:uid")
    Single<Integer> countItemInCart(String uid);

    @Query("SELECT SUM((foodPrice+foodExtraPrice) *foodQuantity) FROM Cart WHERE uid=:uid")
    Single<Double> sumPriceInCart(String uid);

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid")
    Single<CartItem> getItemInCart(String foodId,String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItem(CartItem cartItems);

    @Delete
    Single<Integer> deleteCartItem(CartItem cartItems);

    @Query("DELETE FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart(String uid);

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid AND foodSize=:foodSize AND foodAddon=:foodAddon")
    Single<CartItem> getItemAllOptionsInCart(String uid,String foodId,String foodSize,String foodAddon);

}
