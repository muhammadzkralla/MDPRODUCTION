package com.vuducminh.nicefood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.andremion.counterfab.CounterFab;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.common.CommonAgr;
import com.vuducminh.nicefood.database.CartDataSource;
import com.vuducminh.nicefood.database.CartDatabase;
import com.vuducminh.nicefood.database.LocalCartDataSource;
import com.vuducminh.nicefood.eventbus.BestDealItemClick;
import com.vuducminh.nicefood.eventbus.CategoryClick;
import com.vuducminh.nicefood.eventbus.CountCartEvent;
import com.vuducminh.nicefood.eventbus.FoodItemClick;
import com.vuducminh.nicefood.eventbus.HideFABCart;
import com.vuducminh.nicefood.eventbus.MenuItemBack;
import com.vuducminh.nicefood.eventbus.MenuItemEvent;
import com.vuducminh.nicefood.eventbus.PopluarCategoryClick;
import com.vuducminh.nicefood.model.CategoryModel;
import com.vuducminh.nicefood.model.FoodModel;
import com.vuducminh.nicefood.model.UserModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavController navController;
    private CartDataSource cartDataSource;
    private NavigationView navigationView;

    private android.app.AlertDialog dialog;

    int menuClickId = -1;

    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    @BindView(R.id.fab)
    CounterFab fab;

    @Override
    protected void onResume() {
        super.onResume();
        coutCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initPlaceClient();

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        ButterKnife.bind(this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> navController.navigate(R.id.nav_cart));
        drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_restaurant,
                R.id.nav_home, R.id.nav_menu, R.id.nav_food_detail,
                R.id.nav_view_orders, R.id.nav_cart, R.id.nav_food_list)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        View headerView = navigationView.getHeaderView(0);
        TextView tv_user = (TextView) headerView.findViewById(R.id.tv_user);
        Common.setSpanString("Hey, ",Common.currentUser.getName(),tv_user);

        coutCartItem();
    }

    private void initPlaceClient() {
        Places.initialize(this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setCheckable(true);
        drawer.closeDrawers();
        switch (item.getItemId()) {
            case R.id.nav_restaurant:
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_restaurant);
                }
                break;

            case R.id.nav_home: {
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_home);
                }
                break;
            }
            case R.id.nav_menu: {
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_menu);
                }
                break;
            }
            case R.id.nav_cart: {
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_cart);
                }
                break;
            }
            case R.id.nav_view_orders: {
                if(item.getItemId() != menuClickId) {
                    navController.navigate(R.id.nav_view_orders);
                }
                break;
            }
            case R.id.nav_sign_out: {
                signOut();
                break;
            }
            case R.id.nav_update_info: {
                showUpdateInfoDialog();
                break;
            }
            case R.id.nav_contact_us:{
                if(item.getItemId() != menuClickId) {
                    Intent contact = new Intent(HomeActivity.this, ContactUs.class);
                    startActivity(contact);
                }
                break;
            }

            case R.id.nav_about_us:{
                if(item.getItemId() != menuClickId) {
                    Intent about = new Intent(HomeActivity.this, AboutUs.class);
                    startActivity(about);
                }
                break;
            }


        }
        menuClickId = item.getItemId();
        return true;
    }

    private void showUpdateInfoDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Update Info");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.et_name);
        TextView tv_address_detail = (TextView)itemView.findViewById(R.id.tv_address_detail);

        EditText edt_phone = (EditText) itemView.findViewById(R.id.et_phone);

        //places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                //.findFragmentById(R.id.places_autocomplete_fragment);

        /**places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                tv_address_detail.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(HomeActivity.this,""+status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            }
        });**/



        edt_phone.setText(Common.currentUser.getPhone());
        tv_address_detail.setText(Common.currentUser.getAddress());

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(placeSelected != null) {
                    if(TextUtils.isEmpty(edt_name.getText().toString())) {
                        Toast.makeText(HomeActivity.this,"Plaeasr enter your name",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String,Object> update_date = new HashMap<>();
                    update_date.put("name",edt_name.getText().toString());
                    update_date.put("address",tv_address_detail.getText().toString());
                    update_date.put("lat",placeSelected.getLatLng().latitude);
                    update_date.put("lng",placeSelected.getLatLng().longitude);

                    FirebaseDatabase.getInstance()
                            .getReference(CommonAgr.USER_REFERENCES)
                            .child(Common.currentUser.getUid())
                            .updateChildren(update_date)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnSuccessListener(aVoid -> {
                                    dialog.dismiss();
                                Toast.makeText(HomeActivity.this,"Update Info success",Toast.LENGTH_LONG).show();
                                Common.currentUser.setName(update_date.get("name").toString());
                                Common.currentUser.setAddress(update_date.get("address").toString());
                                Common.currentUser.setLat(Double.valueOf(update_date.get("lat").toString()));
                                Common.currentUser.setLng(Double.valueOf(update_date.get("lng").toString()));

                            });
                }
            }
        });


        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialog1 -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(places_fragment);
            fragmentTransaction.commit();
        });
        dialog.show();

    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Signout")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Common.selectedFood = null;
                        Common.categorySelected = null;
                        Common.currentUser = null;

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // EventBus

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event) {
        if(event.isSuccess()) {
            navController.navigate(R.id.nav_food_list);
        }
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onFoodSelected(FoodItemClick event) {
        if(event.isSuccess()) {
           navController.navigate(R.id.nav_food_detail);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABCart event) {
        if(event.isHidden()) {
            fab.hide();
        }
        else {
            fab.show();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onBestDealItemClick(BestDealItemClick event) {
        if(event.getBestDealModel() != null) {

            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(CommonAgr.CATEGORY_REF)
                    .child(event.getBestDealModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Common.categorySelected = dataSnapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setMenu_id(dataSnapshot.getKey());

                                FirebaseDatabase.getInstance()
                                        .getReference(CommonAgr.CATEGORY_REF)
                                        .child(event.getBestDealModel().getMenu_id())
                                        .child(CommonAgr.FOOD_REF)
                                        .orderByChild("id")
                                        .equalTo(event.getBestDealModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    for (DataSnapshot itemSnapshot:dataSnapshot.getChildren()) {
                                                        Common.selectedFood = itemSnapshot.getValue(FoodModel.class);
                                                        Common.selectedFood.setKey(itemSnapshot.getKey());
                                                    }
                                                    navController.navigate(R.id.nav_food_detail);
                                                }
                                                else {
                                                    Toast.makeText(HomeActivity.this,"Item doesn't exists",Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this,"Item doesn't exists",Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPopularItemClick(PopluarCategoryClick event) {
        if(event.getPopluarCategoryModel() != null) {

            dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(CommonAgr.CATEGORY_REF)
                    .child(event.getPopluarCategoryModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Common.categorySelected = dataSnapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setMenu_id(dataSnapshot.getKey());

                                FirebaseDatabase.getInstance()
                                        .getReference(CommonAgr.CATEGORY_REF)
                                        .child(event.getPopluarCategoryModel().getMenu_id())
                                        .child(CommonAgr.FOOD_REF)
                                        .orderByChild("id")
                                        .equalTo(event.getPopluarCategoryModel().getFood_id())
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
                                                    for (DataSnapshot itemSnapshot:dataSnapshot.getChildren()) {
                                                        Common.selectedFood = itemSnapshot.getValue(FoodModel.class);
                                                        Common.selectedFood.setKey(itemSnapshot.getKey());

                                                    }
                                                    navController.navigate(R.id.nav_food_detail);
                                                }
                                                else {
                                                    Toast.makeText(HomeActivity.this,"Item doesn't exists",Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                dialog.dismiss();
                                                Toast.makeText(HomeActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this,"Item doesn't exists",Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    private void countCartAgain(CountCartEvent event) {
        if(event.isSuccess()) {
            coutCartItem();
        }
    }


    // Bắt event counter cart
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCartCounter(CountCartEvent event) {
        if(event.isSuccess()) {
           coutCartItem();
        }
    }

    private void coutCartItem() {
        cartDataSource.countItemInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        fab.setCount(integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!e.getMessage().contains("query returned empty")) {
                           // Toast.makeText(HomeActivity.this,"[COUNT CART]"+e.getMessage(),Toast.LENGTH_SHORT).show();
                           // Log.e("Count_Error",e.getMessage());
                        }
                        else{
                            fab.setCount(0);
                        }
                    }
                });
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMenuItemBack(MenuItemBack event) {
        menuClickId = 1;
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onRestaurantClick(MenuItemEvent event) {

        Bundle bundle = new Bundle();
        bundle.putString("restaurant",event.getRestaurantModel().getUid());
        navController.navigate(R.id.nav_home,bundle);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.restaurant_detail_menu);
        }
    }
