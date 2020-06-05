package com.vuducminh.nicefood.ui.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.vuducminh.nicefood.adapter.MyBestdealAdapter;
import com.vuducminh.nicefood.adapter.MyCategoriesAdapter;
import com.vuducminh.nicefood.adapter.MyPopularCategoriesAdapter;
import com.vuducminh.nicefood.R;
import com.vuducminh.nicefood.common.CommonAgr;
import com.vuducminh.nicefood.common.SpacesIiemDecoration;
import com.vuducminh.nicefood.model.PopluarCategoryModel;
import com.vuducminh.nicefood.ui.menu.MenuViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    //define the constructor
    private MenuViewModel menuViewModel;
    //initialize the adapter
    MyCategoriesAdapter adapter;

    Unbinder unbinder;
    //init the views
    @BindView(R.id.recycler_popluar)
    RecyclerView recycler_popluar;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    @BindView(R.id.viewpager)
    LoopingViewPager viewPager;

    LayoutAnimationController layoutAnimationController;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Objects.requireNonNull((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorGold3)));
        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorGold4));
        //declare the Constructor Class
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,root);

        String key = getArguments().getString("restaurant");


        init();
        homeViewModel.getPopularList(key).observe(this,popluarCategoryModels -> {

            //Tạo adapter
            MyPopularCategoriesAdapter adapter = new MyPopularCategoriesAdapter(getContext(),popluarCategoryModels);
            recycler_popluar.setAdapter(adapter);
            recycler_popluar.setLayoutAnimation(layoutAnimationController);
        });
        //Inflate the Item to the adapter
        menuViewModel.getCategoryList().observe(this,categoryModels -> {
            adapter = new MyCategoriesAdapter(getContext(),categoryModels);
            recycler_menu.setAdapter(adapter);
        });

        homeViewModel.getBestDealList(key).observe(this,bestDealModels -> {
            MyBestdealAdapter adapter = new MyBestdealAdapter(getContext(),bestDealModels,true);
            viewPager.setAdapter(adapter);
        });
        return root;
    }

    private void init() {
        //redesign it
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null) {
                    switch (adapter.getItemViewType(position)) {
                        case CommonAgr.DEFAULT_COLUMN_COUNT: return 1;
                        case CommonAgr.FULL_WIDTH_COLUMN: return 2;
                        default: return -1;
                    }
                }
                return -1;
            }
        });
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new SpacesIiemDecoration(8));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recycler_popluar.setHasFixedSize(true);
        recycler_popluar.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        viewPager.pauseAutoScroll();
        super.onPause();
    }
}