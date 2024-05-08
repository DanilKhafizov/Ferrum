package com.khafizov.ferrum.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khafizov.ferrum.fragments.ScheduleInfoFragment;
import com.khafizov.ferrum.R;

public class ScheduleActivity extends AppCompatActivity {
    private LinearLayout dateContainer;
    private ImageButton backBtn;
    private TextView chooseDateTv;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        init();
        listeners();
    }

    private void init(){
        dateContainer = findViewById(R.id.dateContainer);
        backBtn = findViewById(R.id.back_btn);
        chooseDateTv = findViewById(R.id.choose_date_tv);
    }
    private void listeners(){
        backBtn.setOnClickListener(v -> showMainActivity());
        addDateButton("06-12-2023");
        addDateButton("07-12-2023");
        addDateButton("08-12-2023");
        addDateButton("09-12-2023");
        addDateButton("10-12-2023");
        addDateButton("11-12-2023");
        addDateButton("12-12-2023");
        addDateButton("13-12-2023");
        addDateButton("14-12-2023");
        addDateButton("15-12-2023");
        addDateButton("16-12-2023");
        addDateButton("17-12-2023");
        addDateButton("18-12-2023");
        addDateButton("19-12-2023");
        addDateButton("20-12-2023");
        addDateButton("21-12-2023");
        addDateButton("22-12-2023");
        addDateButton("23-12-2023");
        addDateButton("24-12-2023");
        addDateButton("25-12-2023");
        addDateButton("26-12-2023");
        addDateButton("27-12-2023");
        addDateButton("28-12-2023");
        addDateButton("29-12-2023");
        addDateButton("30-12-2023");
        addDateButton("31-12-2023");
    }
    private void addDateButton(String date) {
        int color = ContextCompat.getColor(this, R.color.lavender);
        Button button = new Button(this);
        button.setText(date);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 15, 0);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(color);
        button.setOnClickListener(v -> showTrainingInfo(date));  dateContainer.addView(button); }

    private void showTrainingInfo(String date) {
        Fragment fragment = new ScheduleInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dateFragmentContainer, fragment)
                .commit();
        chooseDateTv.setVisibility(View.GONE);
    }

    public void showMainActivity()
    {
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}