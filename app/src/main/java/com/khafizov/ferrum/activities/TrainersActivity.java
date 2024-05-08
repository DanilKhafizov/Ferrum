package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khafizov.ferrum.fragments.AddTrainerFragment;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.adapters.TrainersAdapter;
import com.khafizov.ferrum.models.Trainers;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class TrainersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final List<Trainers> trainersList = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private TrainersAdapter.OnItemClickListener itemClickListener;
    private ImageButton ImAddTrainer;
    private ProgressBar progressBar;
    private ImageButton backButton;
    private BottomNavigationView bottomNavigationView;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);
        init();
        listeners();
    }
    private void init(){
        recyclerView = findViewById(R.id.recyclerView);
        preferenceManager = new PreferenceManager(getApplicationContext());
        ImAddTrainer = findViewById(R.id.im_add_trainer);
        progressBar = findViewById(R.id.progress_bar);
        backButton = findViewById(R.id.back_btn);
        bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_trainers);
    }

    @SuppressLint("NonConstantResourceId")
    private void listeners(){
        loadTrainers();
        adminListeners();
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                showMainActivity();
            }
        });
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


    private void adminListeners(){
        String role = preferenceManager.getString(Constants.KEY_ROLE);
        if(role.equals("Администратор")){
            ImAddTrainer.setVisibility(View.VISIBLE);
            ImAddTrainer.setOnClickListener(v -> showAddTrainerFragment());
        }
    }

    private void loadTrainers(){
        itemClickListener = position -> {
        String selectedTrainerId = trainersList.get(position).getId();
        preferenceManager.putString(Constants.KEY_TRAINER_ID, selectedTrainerId);
        openTrainerActivity(selectedTrainerId);};
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EMPLOYEES)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            List<Trainers> fetchedTrainers = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Trainers trainer = new Trainers();
                trainer.setId(document.getId());
                trainer.setNameTrainer(document.getString(Constants.KEY_NAME));
                trainer.setRoleTrainer(document.getString(Constants.KEY_ROLE));
                trainer.setImageTrainer(document.getString(Constants.KEY_IMAGE));
                 fetchedTrainers.add(trainer);
                 if (fetchedTrainers.size() == queryDocumentSnapshots.size()) {
                     trainersList.clear();
                     trainersList.addAll(fetchedTrainers);
                     TrainersAdapter trainersAdapter = new TrainersAdapter(trainersList, itemClickListener);
                     recyclerView.setAdapter(trainersAdapter);
                 }}progressBar.setVisibility(View.GONE);})
        .addOnFailureListener(e -> showToast("Ошибка при получении данных: " + e.getMessage()));}

    private void openTrainerActivity(String dishId) {
        Trainers selectedTrainer = null;
        for (Trainers trainer : trainersList) {
            if (trainer.getId().equals(dishId)) {
                selectedTrainer = trainer;
                break;
            }
        }
        if (selectedTrainer != null) {
            preferenceManager.putString(Constants.KEY_TRAINER_ID, selectedTrainer.getId());
            Intent intent = new Intent(this, TrainerActivity.class);
            intent.putExtra(Constants.KEY_TRAINER_ID, selectedTrainer.getId());
            startActivity(intent);
        }
    }


    public void showMainActivity()
    {
        Intent intent = new Intent(TrainersActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TrainersActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showAddTrainerFragment() {
        AddTrainerFragment addTrainerFragment = new AddTrainerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_trainers_container, addTrainerFragment);
        fragmentTransaction.addToBackStack(null); // Добавление в стек возврата для обратной навигации
        fragmentTransaction.commit();
    }

}