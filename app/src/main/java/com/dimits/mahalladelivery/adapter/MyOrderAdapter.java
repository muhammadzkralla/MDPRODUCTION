package com.dimits.mahalladelivery.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dimits.mahalladelivery.callback.IRecyclerClickListener;
import com.dimits.mahalladelivery.common.Common;
import com.dimits.mahalladelivery.database.CartItem;
import com.dimits.mahalladelivery.model.OrderModel;
import com.dimits.mahalladelivery.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>{

    private Context context;
    private List<OrderModel> orderModelList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.layout_order_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderModel orderModel = orderModelList.get(position);
        Glide.with(context).load(orderModel.getCartItemList().get(0).getFoodImage()).into(holder.img_order);
        calendar.setTimeInMillis(orderModel.getCreateDate());
        Date date = new Date(orderModel.getCreateDate());
        holder.tv_order_date.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
        .append("")
        .append(simpleDateFormat.format(date)));
        holder.tv_order_number.setText(new StringBuilder("OrderModel number: ").append(orderModel.getOrderNumber()));
        holder.tv_order_comment.setText(new StringBuilder("Comment: ").append(orderModel.getCommet()));
        holder.tv_order_status.setText(new StringBuilder("Status: ").append(Common.convertStatusToText(orderModel.getOrderStatus())));
        holder.tv_order_price.setText(new StringBuilder("Price: ").append(orderModel.getFinalPayment()));

        holder.setRecyclerClickListener((view, pos) ->

                showDialog(orderModelList.get(pos).getCartItemList()));
    }
    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout_dialog);
        Button btn_ok = (Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail = (RecyclerView)layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(context,layoutManager.getOrientation()));
        MyOrderDetailAdapter myOrderDetailAdapter = new MyOrderDetailAdapter(context,cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);


        //show dialog
        AlertDialog dialog =builder.create();
        dialog.show();
        //custom dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public OrderModel getItemAtPosition(int position) {
        return orderModelList.get(position);
    }

    public void setItemAtPosition(int position, OrderModel item) {
        orderModelList.set(position,item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Unbinder unbinder;
        IRecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        @BindView(R.id.img_order)
        ImageView img_order;
        @BindView(R.id.tv_order_date)
        TextView tv_order_date;
        @BindView(R.id.tv_order_number)
        TextView tv_order_number;
        @BindView(R.id.tv_order_comment)
        TextView tv_order_comment;
        @BindView(R.id.tv_order_status)
        TextView tv_order_status;
        @BindView(R.id.tv_order_price)
        TextView tv_order_price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClickListener(view,getAdapterPosition());
        }
    }
}
