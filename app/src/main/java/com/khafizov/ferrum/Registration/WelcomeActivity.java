package com.khafizov.ferrum.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class WelcomeActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }



    }

    public void reg_btn_Click(View view)
    {
        Intent intent = new Intent(WelcomeActivity.this, RegActivity.class);
        startActivity(intent);
    }

    public void reg1_btn_Click(View view)
    {
        Intent intent = new Intent(WelcomeActivity.this, EnterActivity.class);
        startActivity(intent);
    }

}