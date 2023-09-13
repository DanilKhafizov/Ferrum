package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.khafizov.ferrum.Fragments.LanguageFragment;
import com.khafizov.ferrum.Fragments.StyleFragment;
import com.khafizov.ferrum.Fragments.ThemeFragment;
import com.khafizov.ferrum.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
}