package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khafizov.ferrum.R;

public class TrainersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_trainers);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.bottom_trainers:
                    return true;
                case R.id.bottom_services:
                    startActivity(new Intent(getApplicationContext(), ServicesActivity.class));
                    finish();
                    return true;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
            }
            return false;
        });
    }

}