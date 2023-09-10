package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khafizov.ferrum.Fragments.JasonFragment;
import com.khafizov.ferrum.Fragments.PavelFragment;
import com.khafizov.ferrum.Fragments.SarahFragment;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.Registration.EnterActivity;
import com.khafizov.ferrum.Registration.RegActivity;
import com.khafizov.ferrum.Registration.WelcomeActivity;

public class TrainersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);

        ImageButton backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(v -> {
       FragmentManager fragmentManager = getSupportFragmentManager();
       if (fragmentManager.getBackStackEntryCount() > 0) {
           fragmentManager.popBackStack();
       } else {
           showMainActivity();
       }

//            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            if (currentFragment != null) {
//                getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
//            } else {
//                showMainActivity();
//            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_trainers);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    return true;
                case R.id.bottom_trainers:
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

    public void pavel_card_Click(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new PavelFragment()).addToBackStack(null).commit();
    }

    public void jason_card_Click(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new JasonFragment()).addToBackStack(null).commit();
    }

    public void sarah_card_Click(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new SarahFragment()).addToBackStack(null).commit();
    }


    public void showMainActivity()
    {
        Intent intent = new Intent(TrainersActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}