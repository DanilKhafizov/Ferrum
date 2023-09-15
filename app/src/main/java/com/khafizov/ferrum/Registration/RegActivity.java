package com.khafizov.ferrum.Registration;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.khafizov.ferrum.R;

public class RegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

    }
    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }
}
