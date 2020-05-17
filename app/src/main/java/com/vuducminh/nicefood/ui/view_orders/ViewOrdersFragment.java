package com.vuducminh.nicefood.ui.view_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.adapter.MyOrderAdapter;
import com.vuducminh.nicefood.callback.ILoadOrderCallbackListener;
import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.common.CommonAgr;
import com.vuducminh.nicefood.common.MySwiperHelper;
import com.vuducminh.nicefood.eventbus.MenuItemBack;
import com.vuducminh.nicefood.model.OrderModel;
import com.vuducminh.nicefood.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {


    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;

    private Unbinder unbinder;

    private ViewOrdersViewModel viewOrdersViewModel;

    private AlertDialog dialog;
    private ILoadOrderCallbackListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                ViewModelProviders.of(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order, container, false);
        unbinder = ButterKnife.bind(this,root);
        loadOrdersFromFirebase();
        initViews(root);

        viewOrdersViewModel.getMutableLiveDataOrderList().observe(this,orderList -> {
            MyOrderAdapter adapter = new MyOrderAdapter(getContext(),orderList);
            recycler_orders.setAdapter(adapter);
        });
        return root;
    }

    private void loadOrdersFromFirebase() {
        List<OrderModel> orderModelList = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference(Common.RESTAURANT_REF)
                .child(Common.currentRestaurant.getUid())
                .child(CommonAgr.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot orderSnapShot:dataSnapshot.getChildren()) {
                            OrderModel orderModel = orderSnapShot.getValue(OrderModel.class);
                            orderModel.setOrderNumber(orderSnapShot.getKey());
                            orderModelList.add(orderModel);
                        }
                        listener.onLoadOrderLoadSuccess(orderModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onLoadOrderLoadFailed(databaseError.getMessage());
                    }
                });
    }

    private void initViews(View root) {
        listener = this;
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_orders, 250) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {

                buf.add(new MyButton(getContext(), "Cancel OrderModel", 30, 0, Color.parseColor("#FF3C30"),
                        position -> {
                            OrderModel orderModel = ((MyOrderAdapter)recycler_orders.getAdapter()).getItemAtPosition(position);

                            if(orderModel.getOrderStatus() == 0) {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                                builder.setTitle("Cancel Order")
                                        .setMessage("Do you really want to cancel this order?")
                                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                        .setPositiveButton("YES", (dialog, which) -> {
                                            Map<String,Object> update_data = new HashMap<>();
                                            update_data.put("orderStatus",-1);
                                            FirebaseDatabase.getInstance()
                                                    .getReference(CommonAgr.ORDER_REF)
                                                    .child(orderModel.getOrderNumber())
                                                    .updateChildren(update_data)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnSuccessListener(aVoid -> {
                                                        orderModel.setOrderStatus(-1);
                                                        ((MyOrderAdapter)recycler_orders.getAdapter()).setItemAtPosition(position,orderModel);
                                                        recycler_orders.getAdapter().notifyItemChanged(position);
                                                        Toast.makeText(getContext(),"Cancel order successfully",Toast.LENGTH_SHORT).show();
                                                    });
                                        });

                                androidx.appcompat.app.AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                            else {
                                Toast.makeText(getContext(),new StringBuilder("You order was changed to ")
                                .append(Common.convertStatusToText(orderModel.getOrderStatus()))
                                .append(", so you can't cancel it!"),Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        };

    }


    @Override
    public void onLoadOrderLoadSuccess(List<OrderModel> orderModelModels) {
        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderModelModels);
    }

    @Override
    public void onLoadOrderLoadFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}