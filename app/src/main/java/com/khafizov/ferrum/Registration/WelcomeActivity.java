package com.khafizov.ferrum.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.khafizov.ferrum.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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