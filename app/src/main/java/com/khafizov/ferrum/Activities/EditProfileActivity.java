package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.khafizov.ferrum.R;


public class EditProfileActivity extends AppCompatActivity {

    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> showProfileActivity() );
    }
    public void showProfileActivity()
    {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}