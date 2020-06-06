package com.vuducminh.nicefood.ui.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vuducminh.nicefood.R;
import com.vuducminh.nicefood.adapter.MyRestaurantAdapter;
import com.vuducminh.nicefood.eventbus.CountCartEvent;
import com.vuducminh.nicefood.eventbus.HideFABCart;
import com.vuducminh.nicefood.eventbus.MenuInflateEvent;
import com.vuducminh.nicefood.model.RestaurantModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RestaurantFragment extends Fragment {

    private RestaurantViewModel mViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_restaurant)
    RecyclerView recycler_restaurant;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyRestaurantAdapter adapter;

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Objects.requireNonNull((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGold3)));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorGold4));

        mViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);
       View root =  inflater.inflate(R.layout.fragment_restaurant, container, false);
       unbinder = ButterKnife.bind(this,root);
       initViews();
       mViewModel.getMessageError().observe(this,message->{
           Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
           dialog.dismiss();
        });
       mViewModel.getRestaurantListMutable().observe(this,restaurantModes -> {
           dialog.dismiss();
           adapter = new MyRestaurantAdapter(getContext(), restaurantModes);
           recycler_restaurant.setAdapter(adapter);
           recycler_restaurant.setLayoutAnimation(layoutAnimationController);
       });
       return root;
    }

    private void initViews() {
        EventBus.getDefault().postSticky(new HideFABCart(true));// Hide when user back to this fragment
        setHasOptionsMenu(true);
        dialog =  new AlertDialog.Builder(getContext()).setCancelable(false)
                .setMessage("Please wait...").create();
        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_restaurant.setLayoutManager(linearLayoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(getContext(),linearLayoutManager.getOrientation()));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().postSticky(new CountCartEvent(true));
        EventBus.getDefault().postSticky(new MenuInflateEvent(false));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);

    }
}
