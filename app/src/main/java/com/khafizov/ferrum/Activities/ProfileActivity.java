package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khafizov.ferrum.R;

public class ProfileActivity extends AppCompatActivity {

    ImageButton langBtn, themeBtn, styleBtn, backBtn;
    Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        langBtn = findViewById(R.id.go_lang_im_btn);
        themeBtn = findViewById(R.id.go_theme_im_btn);
        styleBtn = findViewById(R.id.go_style_im_btn);
        editBtn = findViewById(R.id.edit_btn);
        backBtn = findViewById(R.id.back_btn);


        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        langBtn.setOnClickListener(v -> showSettingsActivity() );
        themeBtn.setOnClickListener(v -> showSettingsActivity() );
        styleBtn.setOnClickListener(v -> showSettingsActivity() );
        editBtn.setOnClickListener(v -> showEditProfileActivity() );
        backBtn.setOnClickListener(v -> showMainActivity() );




        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.bottom_trainers:
                    startActivity(new Intent(getApplicationContext(), TrainersActivity.class));
                    finish();
                    return true;
                case R.id.bottom_services:
                    startActivity(new Intent(getApplicationContext(), ServicesActivity.class));
                    finish();
                    return true;
                case R.id.bottom_profile:
                    return true;
            }
            return false;
        });
    }
    public void showSettingsActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void showEditProfileActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMainActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}