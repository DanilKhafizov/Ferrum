package com.khafizov.ferrum.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.adapters.ImageSliderAdapter;
import com.khafizov.ferrum.models.User;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager = new PreferenceManager(getApplicationContext());

        ViewPager viewPager = findViewById(R.id.viewPager);
        int[] images = {R.drawable.pavel, R.drawable.jason, R.drawable.sarah};
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(adapter);

//        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
//        if (sharedPreferences.contains("name")) {
//            String name = sharedPreferences.getString("name", "");
//            String surname = sharedPreferences.getString("surname", "");
//            String email = sharedPreferences.getString("email", "");
//            User user = new User(name, surname, email);
//            // Сохраните данные пользователя в глобальной переменной или ViewModel
//        } else {
//            // Загрузите данные пользователя из Firestore и сохраните в SharedPreferences
//        }

//        firestore.loadUserData(new FirestoreCallback() {
//            @Override
//            public void onCallback(User user) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("name", user.getName());
//                editor.putString("email", user.getEmail());
//                editor.apply();
//                // Сохраните данные пользователя в глобальной переменной или ViewModel
//                viewModel.setUser(user); // Пример использования ViewModel
//            }
//        });
//    }

        startImageSliderAutoScroll(viewPager, images.length);



        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
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
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
            }
            return false;
        });
    }


    private int currentPage = 0;

    private void startImageSliderAutoScroll(ViewPager viewPager, int imageCount) {
        final Handler handler = new Handler();
        final Runnable update = () -> {
            if (currentPage == imageCount) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 4000); // Интервал прокрутки в миллисекундах
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }


    }

