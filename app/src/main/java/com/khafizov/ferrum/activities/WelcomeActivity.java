package com.khafizov.ferrum.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class WelcomeActivity extends AppCompatActivity {
    MaterialButton signUpBtn, signInBtn;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
        listeners();
    }
    private void init(){
        signUpBtn = findViewById(R.id.sign_up_btn);
        signInBtn = findViewById(R.id.sign_in_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }
    private void listeners(){
        signUpBtn.setOnClickListener(v -> showSignUpActivity());
        signInBtn.setOnClickListener(v -> showSignInActivity());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void showSignUpActivity()
    {
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
    public void showSignInActivity()
    {
        Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}