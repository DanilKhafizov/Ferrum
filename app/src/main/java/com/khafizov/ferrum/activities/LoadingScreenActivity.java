package com.khafizov.ferrum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class LoadingScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        Handler handler = new Handler();
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)) {
            handler.postDelayed(() -> {
                Intent intent = new Intent(LoadingScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }
        if(!preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)) {
            handler.postDelayed(() -> {
                Intent intent = new Intent(LoadingScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 5000);
        }
    }
}