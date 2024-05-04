package com.example.natterchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.natterchatapp.R;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.example.natterchatapp.utilities.ShowToast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {

    private Preference pref;
    private ShowToast toast;
    private RoundedImageView rivProfile;
    private AppCompatImageView acivLogout;
    private TextView tvHomeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        init();
        loadUser();
        toast.showToast("Welcome "+pref.getString(KEYS.KEY_USER_NAME));
        getToken();

        acivLogout.setOnClickListener(v->logout());

    }

    private void logout(){

        toast.showToast("Logging Out....");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference dr=db.collection(KEYS.KEY_COLLECTION_USER)
                .document(
                        pref.getString(KEYS.KEY_USER_ID)
                );
        HashMap<String, Object> updates= new HashMap<>();
        updates.put(KEYS.KEY_FCM_TOKEN, FieldValue.delete());
        dr.update(updates)
                .addOnSuccessListener(unused -> {
                    pref.clear();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e-> toast.showToast("Unable to sign out"));
    }

    private void loadUser(){
        tvHomeName.setText(pref.getString(KEYS.KEY_USER_NAME));
        byte[] bytes= Base64.decode(pref.getString(KEYS.KEY_USER_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        rivProfile.setImageBitmap(bitmap);

    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference dr=db.collection(KEYS.KEY_COLLECTION_USER)
                .document(
                        pref.getString(KEYS.KEY_USER_ID)
                );
        dr.update(KEYS.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused ->{toast
                        .showToast("Token Yes");} )
                .addOnFailureListener(e->{toast.showToast("Token no");});
    }

    private void init(){
        pref=new Preference(HomeActivity.this);
        toast=new ShowToast(HomeActivity.this);

        rivProfile=findViewById(R.id.rivProfile);
        acivLogout=findViewById(R.id.acivLogout);
        tvHomeName=findViewById(R.id.tvHomeName);


    }
}