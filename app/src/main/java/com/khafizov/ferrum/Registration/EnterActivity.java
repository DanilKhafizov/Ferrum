package com.khafizov.ferrum.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.R;

public class EnterActivity extends AppCompatActivity {
    Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

    }


    public void reg_btn_Click(View view)
    {
        Intent intent = new Intent(EnterActivity.this, RegActivity.class);
        startActivity(intent);
        finish();
    }
    public void enter_btn_Click(View view)
    {
        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }

}
