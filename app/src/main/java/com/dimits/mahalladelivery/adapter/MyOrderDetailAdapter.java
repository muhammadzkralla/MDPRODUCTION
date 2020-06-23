package com.dimits.mahalladelivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dimits.mahalladelivery.R;
import com.dimits.mahalladelivery.database.CartItem;
import com.dimits.mahalladelivery.model.AddonModel;
import com.dimits.mahalladelivery.model.OrderModel;
import com.dimits.mahalladelivery.model.SizeModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.MyViewHOlder> {
    Context context;
    List<CartItem> cartItemList;
    Gson gson;
    private OrderModel orderModel;

    public MyOrderDetailAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        gson = new Gson();
    }

    @NonNull
    @Override
    public MyViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHOlder(LayoutInflater.from(context).inflate(R.layout.layout_order_detail_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHOlder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage()).centerCrop().into(holder.img_food_img);
        holder.txt_food_quantity.setText(new StringBuilder("Quantity: ").append(cartItemList.get(position).getFoodQuantity()));
        holder.txt_food_name.setText(cartItemList.get(position).getFoodName());
        holder.txt_phone.setText(cartItemList.get(position).getUserPhone());
        holder.txt_price.setText(new StringBuilder("Price : ").append(cartItemList.get(position).getFoodPrice() + cartItemList.get(position).getFoodExtraPrice()).toString());
        if (!cartItemList.get(position).getFoodSize().equals("Default")) {
            SizeModel sizeModel = gson.fromJson(cartItemList.get(position).getFoodSize(), new TypeToken<SizeModel>() {}.getType());
            if (sizeModel != null) {
                holder.txt_size.setText(new StringBuilder("Size: ").append(sizeModel.getName()));
            }
        }
        else{
            holder.txt_size.setText(new StringBuilder("Size: Default"));
        }
        if (!cartItemList.get(position).getFoodAddon().equals("Default")){
            List<AddonModel> addonModels = gson.fromJson(cartItemList.get(position).getFoodAddon(),new TypeToken<List<AddonModel>>(){}.getType());
            StringBuilder addonString = new StringBuilder();
            if (addonModels != null){
                for (AddonModel addonModel : addonModels){
                    addonString.append(addonModel.getName()).append(",  ");
                    addonString.delete(addonString.length()-1,addonString.length()); // Remove Last Character
                    holder.txt_food_add_on.setText(new StringBuilder("Addon: ").append(addonString));

                }
            }
        }else{
            holder.txt_food_add_on.setText(new StringBuilder("Addon: Default"));
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHOlder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_food_name)
        TextView txt_food_name;

        @BindView(R.id.text_food_add_on)
        TextView txt_food_add_on;

        @BindView(R.id.txt_size)
        TextView txt_size;

        @BindView(R.id.txt_food_quantity)
        TextView txt_food_quantity;

        @BindView(R.id.img_food_img)
        ImageView img_food_img;

        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @BindView(R.id.txt_price)
        TextView txt_price;




        private Unbinder unbinder;
        //
        MyViewHOlder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
