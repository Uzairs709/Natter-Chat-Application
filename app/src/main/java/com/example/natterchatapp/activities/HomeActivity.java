package com.example.natterchatapp.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.natterchatapp.R;
import com.example.natterchatapp.utilities.KEYS;
import com.example.natterchatapp.utilities.Preference;
import com.example.natterchatapp.utilities.ShowToast;

public class HomeActivity extends AppCompatActivity {

    private Preference pref;
    private ShowToast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        init();
        toast.showToast("Welcome "+pref.getString(KEYS.KEY_USER_NAME));


    }

    private void init(){
        pref=new Preference(HomeActivity.this);
        toast=new ShowToast(HomeActivity.this);
    }
}