package com.khafizov.ferrum.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView profileTv;
    private Spinner spinnerAppeal;
    private EditText descAppeal, phoneAppeal, emailAppeal;
    private Button sendAppealBtn;
    private String title, desc, phone, email;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        init();
        listeners();
    }

    private void init(){
        backBtn = findViewById(R.id.back_btn);
        profileTv = findViewById(R.id.profile_tv);
        spinnerAppeal = findViewById(R.id.spinner_appeal);
        descAppeal = findViewById(R.id.desc_appeal);
        phoneAppeal = findViewById(R.id.phone_appeal);
        emailAppeal = findViewById(R.id.email_appeal);
        sendAppealBtn = findViewById(R.id.send_appeal_btn);
        bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

    }

    @SuppressLint("NonConstantResourceId")
    private void listeners(){
        backBtn.setOnClickListener(v -> showProfileActivity());
        profileTv.setOnClickListener(v -> showProfileActivity());
        createSpinner();
        sendAppealBtn.setOnClickListener(v -> checkFields());
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

    private void createNewCollection(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (title.equals("Отзыв")) {
            //создать коллекцию Firestore "reviews"
            Map<String, Object> reviews = new HashMap<>();
            reviews.put(Constants.KEY_DESC, desc);
            reviews.put(Constants.KEY_PHONE, phone);
            reviews.put(Constants.KEY_EMAIL, email);
            db.collection(Constants.KEY_COLLECTION_REVIEWS)
                    .add(reviews);
            showToast("Отзыв отправлен");
        }
        if (title.equals("Вопрос")){
            //создать коллекцию Firestore "questions"
            Map<String, Object> questions = new HashMap<>();
            questions.put(Constants.KEY_DESC, desc);
            questions.put(Constants.KEY_PHONE, phone);
            questions.put(Constants.KEY_EMAIL, email);
            db.collection(Constants.KEY_COLLECTION_QUESTIONS)
                    .add(questions);
            showToast("Вопрос отправлен");
        }
        showProfileActivity();
    }

    private void showProfileActivity()
    {
        Intent intent = new Intent(FeedBackActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkFields(){
        title = spinnerAppeal.getSelectedItem().toString();
        desc = descAppeal.getText().toString();
        phone = phoneAppeal.getText().toString();
        email = emailAppeal.getText().toString();
        if(!title.isEmpty() && !desc.isEmpty() && !phone.isEmpty() && !email.isEmpty()){
            createNewCollection();
        }
        else{
            showToast("Пожалуйста заполните все поля");
        }
    }

    private void createSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.feedback, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        spinnerAppeal.setAdapter(adapter);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FeedBackActivity.this, ProfileActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


}