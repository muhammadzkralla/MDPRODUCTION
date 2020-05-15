package com.vuducminh.nicefood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vuducminh.nicefood.HomeActivity;
import com.vuducminh.nicefood.R;
import com.vuducminh.nicefood.common.Common;
import com.vuducminh.nicefood.model.UserModel;
import com.vuducminh.nicefood.remote.ICloudFunction;
import com.vuducminh.nicefood.remote.RetrofitICloudClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 9696;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ICloudFunction cloudFunctions;

    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());


        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        cloudFunctions = RetrofitICloudClient.getInstance().create(ICloudFunction.class);
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //Login
                //Toast.makeText(MainActivity.this, "already login",Toast.LENGTH_SHORT).show();
                checkUserFromFirebase(user);
            } else {

                phoneLogin();
            }

        };
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        //dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(MainActivity.this, "You already registed", Toast.LENGTH_SHORT).show();

                            UserModel usersModel = dataSnapshot.getValue(UserModel.class);

                            goToHomeActivity(usersModel);

                        } else {
                            showRegisterDialog(user);
                        }

                        //dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //dialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText etName =  itemView.findViewById(R.id.et_name);
        EditText etAddress = itemView.findViewById(R.id.et_address);
        EditText etPhone = itemView.findViewById(R.id.et_phone);

        //setting
        etPhone.setText(user.getPhoneNumber());

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("REGISTER", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(etName.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(etAddress.getText().toString())) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel userModel = new UserModel();
            userModel.setUid(user.getUid());
            userModel.setName(etName.getText().toString());
            userModel.setAddress(etAddress.getText().toString());
            userModel.setPhone(etPhone.getText().toString());

            userRef.child(user.getUid()).setValue(userModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Congratulation! Register Successfully", Toast.LENGTH_SHORT).show();
                            goToHomeActivity(userModel);
                        }
                    });
        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToHomeActivity(UserModel userModel) {
        Common.currentUser = userModel; //Important,you need always assign value for it before use
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void phoneLogin() {

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers).build(),
                APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else{
                Toast.makeText(this,"Failed sign in!!",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
