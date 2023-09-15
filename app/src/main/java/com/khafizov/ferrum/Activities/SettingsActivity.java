package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.khafizov.ferrum.Fragments.LanguageFragment;
import com.khafizov.ferrum.Fragments.StyleFragment;
import com.khafizov.ferrum.Fragments.ThemeFragment;
import com.khafizov.ferrum.R;

public class SettingsActivity extends AppCompatActivity {

    ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        homeBtn = findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(v -> showMainActivity() );
    }

    public void changeLangBtnClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_settings_container, new LanguageFragment()).addToBackStack(null).commit();
    }

    public void changeThemeBtnClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_settings_container, new ThemeFragment()).addToBackStack(null).commit();
    }

    public void changeStyleBtnClick(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_settings_container, new StyleFragment()).addToBackStack(null).commit();
    }
    public void showMainActivity()
    {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}