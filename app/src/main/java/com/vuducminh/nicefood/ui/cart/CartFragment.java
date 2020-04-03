package com.vuducminh.nicefood.ui.cart;

import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.adapter.MyCartAdapter;
import com.vuducminh.nicefood.callback.ILoadTimeFromFirebaseListener;
import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.common.CommonAgr;
import com.vuducminh.nicefood.common.MySwiperHelper;
import com.vuducminh.nicefood.database.CartDataSource;
import com.vuducminh.nicefood.database.CartDatabase;
import com.vuducminh.nicefood.database.CartItem;
import com.vuducminh.nicefood.database.LocalCartDataSource;
import com.vuducminh.nicefood.eventbus.CountCartEvent;
import com.vuducminh.nicefood.eventbus.HideFABCart;
import com.vuducminh.nicefood.eventbus.MenuItemBack;
import com.vuducminh.nicefood.eventbus.UpdateItemInCart;
import com.vuducminh.nicefood.model.FCMservice.FCMSendData;
import com.vuducminh.nicefood.model.OrderModel;
import com.vuducminh.nicefood.R;
import com.vuducminh.nicefood.remote.IFCMService;
import com.vuducminh.nicefood.remote.RetrofitFCMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener {

    private static final int REQUEST_BAINTREE_CODE = 1999;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Parcelable recyclerViewState;

    private CartViewModel cartViewModel;
    private CartDataSource cartDataSource;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    String address, comment;

    //    ICloudFunction iCloudFunction;
    ILoadTimeFromFirebaseListener iLoadTimeListener;
    IFCMService ifcmService;

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.tv_total_price)
    TextView tv_total_price;
    @BindView(R.id.tv_empty_cart)
    TextView tv_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;

    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("One more step!");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);


        EditText edt_comment = (EditText) view.findViewById(R.id.edt_comment);
        TextView tv_address_detail = (TextView) view.findViewById(R.id.tv_address_detail);
        RadioButton rdi_home = (RadioButton) view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this = (RadioButton) view.findViewById(R.id.rdi_ship_this_address);
        RadioButton rdi_cod = (RadioButton) view.findViewById(R.id.rdi_cod);
        RadioButton rdi_braintree = (RadioButton) view.findViewById(R.id.rdi_braintree);

        places_fragment = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);

        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                tv_address_detail.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(),""+status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            }
        });


        tv_address_detail.setText(Common.currentUser.getAddress());

        rdi_home.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_address_detail.setText(Common.currentUser.getAddress());
                    tv_address_detail.setVisibility(View.VISIBLE);
                    places_fragment.setHint(Common.currentUser.getAddress());
                }
            }
        });
        rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    tv_address_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        rdi_ship_to_this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fusedLocationProviderClient.getLastLocation()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    tv_address_detail.setVisibility(View.GONE);
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    String coordinates = new StringBuilder()
                                            .append(task.getResult().getLatitude())
                                            .append("/")
                                            .append(task.getResult().getLongitude()).toString();

                                    Single<String> singleAddress = Single.just(getAddessFromLatLng(task.getResult().getLatitude(),
                                            task.getResult().getLongitude()));

                                    Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                                        @Override
                                        public void onSuccess(String s) {


                                            tv_address_detail.setText(s);
                                            tv_address_detail.setVisibility(View.VISIBLE);
                                            places_fragment.setHint(s);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                            tv_address_detail.setText(e.getMessage());
                                            tv_address_detail.setVisibility(View.VISIBLE);
                                        }
                                    });

                                }
                            });
                }
            }
        });


        builder.setView(view);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rdi_cod.isChecked()) {
                    paymentCOD(tv_address_detail.getText().toString(), edt_comment.getText().toString());
                }
//                else if(rdi_braintree.isChecked()) {
//                    address = edt_address.getText().toString();
//                    comment = edt_comment.getText().toString();
//
//                    if(!TextUtils.isEmpty((Common.currentToken))) {
//                        DropInRequest dropInRequest = new DropInRequest().clientToken(Common.currentToken);
//                        startActivityForResult(dropInRequest.getIntent(getContext()),REQUEST_BAINTREE_CODE);
//                    }
//                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private MyCartAdapter adapter;

    private Unbinder unbinder;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

//        iCloudFunction = RetrofitICloudClient.getInstance().create(ICloudFunction.class);
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        iLoadTimeListener = this;

        unbinder = ButterKnife.bind(this, root);
        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataItem().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                if (cartItems == null || cartItems.isEmpty()) {

                    recycler_cart.setVisibility(View.GONE);
                    group_place_holder.setVisibility(View.GONE);
                    tv_empty_cart.setVisibility(View.VISIBLE);
                } else {
                    recycler_cart.setVisibility(View.VISIBLE);
                    group_place_holder.setVisibility(View.VISIBLE);
                    tv_empty_cart.setVisibility(View.GONE);

                    adapter = new MyCartAdapter(getContext(), cartItems);
                    recycler_cart.setAdapter(adapter);
                }
            }
        });
        initViews();
        initLocation();
        return root;
    }


    private void initViews() {

        initPlaceView();

        setHasOptionsMenu(true);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        EventBus.getDefault().postSticky(new HideFABCart(true));

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_cart, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {

                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3C30"),
                        position -> {
                            CartItem cartItem = adapter.getItemAtPosition(position);
                            cartDataSource.deleteCartItem(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(position);
                                            sumAllItemCart(); // update total price
                                            EventBus.getDefault().postSticky(new CountCartEvent(true));
                                            Toast.makeText(getContext(), "Delete item from Cart successful!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));
            }
        };

        sumAllItemCart();
    }

    private void initPlaceView() {
        Places.initialize(getContext(),getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());
    }

    private void initLocation() {
        builLocationRequest();
        builLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }


    private void builLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    private void builLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void sumAllItemCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        tv_total_price.setText(new StringBuilder("Total: $").append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty")) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);   // Ẩn home menu
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cart) {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Toast.makeText(getContext(), "Clear Cart Success", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new CountCartEvent(true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateItemInCartEvent(UpdateItemInCart event) {
        if (event.getCartItem() != null) {
            // Đầu tiên, lưu state của RecyclerView
            recyclerViewState = recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItem(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "[UPDAET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        tv_total_price.setText(new StringBuilder("Total: $")
                                .append(Common.formatPrice(price)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!e.getMessage().contains("Query returned empty result set")) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private String getAddessFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            } else {
                result = "Address not found";
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    private void paymentCOD(String address, String comment) {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Double>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Double totalPrice) {
                                double finalPrice = totalPrice; // We will modify formuls discount late
                                OrderModel orderModel = new OrderModel();
                                orderModel.setUserId(Common.currentUser.getUid());
                                orderModel.setUserName(Common.currentUser.getName());
                                orderModel.setUserPhone(Common.currentUser.getPhone());
                                orderModel.setShippingAddress(address);
                                orderModel.setCommet(comment);

                                if (currentLocation != null) {
                                    orderModel.setLat(currentLocation.getLatitude());
                                    orderModel.setLng(currentLocation.getLongitude());
                                } else {
                                    orderModel.setLat(-0.1f);
                                    orderModel.setLng(-0.1f);
                                }

                                orderModel.setCartItemList(cartItems);
                                orderModel.setTotalPayment(totalPrice);
                                orderModel.setDiscount(0);
                                orderModel.setFinalPayment(finalPrice);
                                orderModel.setCod(true);
                                orderModel.setTransactionId("Cash On Delivery");

                                // Submit this orderModel object to Firebase
                                syncLocalTimeWithGlobaltime(orderModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(!e.getMessage().contains("Query returned empty result set")) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }), throwable -> Toast.makeText(getContext(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private void syncLocalTimeWithGlobaltime(OrderModel orderModel) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long offset = dataSnapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE", "" + sdf.format(resultDate));

                iLoadTimeListener.onLoadTimeSuccess(orderModel, estimatedServerTimeMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iLoadTimeListener.onLoadtimeFailed(databaseError.getMessage());
            }
        });
    }

    private void writeOrderToFireBase(OrderModel orderModel) {
        FirebaseDatabase.getInstance()
                .getReference(CommonAgr.ORDER_REF)
                .child(Common.creteOrderNumber())    // Create orderModel number with only digit
                .setValue(orderModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cartDataSource.cleanCart(Common.currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                Map<String, String> notiData = new HashMap<>();
                                notiData.put(CommonAgr.NOTI_TITLE, "New OrderModel");
                                notiData.put(CommonAgr.NOTI_CONTENT, "You have new orderModel from " + Common.currentUser.getPhone());

                                FCMSendData sendData = new FCMSendData(Common.createTopicOrder(), notiData);

                                compositeDisposable.add(ifcmService.sendNotification(sendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            //Clean
                                            Toast.makeText(getContext(), "OrderModel placed Successfully", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CountCartEvent(true));
                                        }, throwable -> {
                                            Toast.makeText(getContext(), "OrderModel was sent but failure to send notification", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CountCartEvent(true));
                                        }));

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onLoadTimeSuccess(OrderModel orderModel, long estimateTimeInMs) {
        orderModel.setCreateDate(estimateTimeInMs);
        orderModel.setOrderStatus(0);
        writeOrderToFireBase(orderModel);
    }

    @Override
    public void onLoadtimeFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQUEST_BAINTREE_CODE && resultCode == Activity.RESULT_OK) {
//            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//            PaymentMethodNonce nonce = result.getPaymentMethodNonce();
//
//            // calculate sum cart
//            cartDataSource.sumPriceInCart(Common.currentUser.getUid())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new SingleObserver<Double>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(Double totalPrice) {
//
//                            // Get all item in cart to create order
//                            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Consumer<List<CartItem>>() {
//                                @Override
//                                public void accept(List<CartItem> cartItems) throws Exception {
//                                    //Submit payment
//                                    compositeDisposable.add(iCloudFunction.submitPayment(totalPrice,
//                                            nonce.getNonce())
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new Consumer<BraintreeTransaction>() {
//                                        @Override
//                                        public void accept(BraintreeTransaction braintreeTransaction) throws Exception {
//                                            if(braintreeTransaction.isSuccess()) {
//                                                double finalPrice = totalPrice; // We will modify formuls discount late
//                                                OrderModel order = new OrderModel();
//                                                order.setUserId(Common.currentUser.getUid());
//                                                order.setUserName(Common.currentUser.getName());
//                                                order.setUserPhone(Common.currentUser.getPhone());
//                                                order.setShippingAddress(address);
//                                                order.setCommet(comment);
//
//                                                if(currentLocation != null) {
//                                                    order.setLat(currentLocation.getLatitude());
//                                                    order.setLng(currentLocation.getLongitude());
//                                                }
//                                                else {
//                                                    order.setLat(-0.1f);
//                                                    order.setLng(-0.1f);
//                                                }
//
//                                                order.setCartItemList(cartItems);
//                                                order.setTotalPayment(totalPrice);
//                                                order.setDiscount(0);
//                                                order.setFinalPayment(finalPrice);
//                                                order.setCod(false);
//                                                order.setTransactionId(braintreeTransaction.getTransaction().getId());
//
//                                                // Submit this order object to Firebase
//                                                writeOrderToFireBase(order);
//                                            }
//                                        }
//                                    },throwable -> {
//                                        Toast.makeText(getContext(),""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
//                                    }));
//                                }
//                            },throwable -> {
//                                Toast.makeText(getContext(),""+throwable.getMessage(),Toast.LENGTH_SHORT).show();
//
//                            }));
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
}