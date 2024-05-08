package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khafizov.ferrum.adapters.UserAdapter;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.User;
import com.khafizov.ferrum.utilities.Constants;

import java.util.ArrayList;
import java.util.List;


public class UsersListActivity extends AppCompatActivity {

    private UserAdapter userAdapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        setupRecyclerView();
        loadAllUsers();
    }
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);
        progressBar = findViewById(R.id.progress_bar);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadAllUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .orderBy(Constants.KEY_REGISTRATION_DATE, Query.Direction.DESCENDING) // Сортировка по полю "registrationDate" по возрастанию
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = new User();
                        user.setId(document.getId());
                        user.setName(document.getString(Constants.KEY_NAME));
                        user.setSurname(document.getString(Constants.KEY_SURNAME));
                        user.setRole(document.getString(Constants.KEY_ROLE));
                        user.setBirthday(document.getString(Constants.KEY_BIRTHDAY));
                        user.setPhone(document.getString(Constants.KEY_PHONE));
                        user.setEmail(document.getString(Constants.KEY_EMAIL));
                        user.setPhoto(document.getString(Constants.KEY_IMAGE));
                        userList.add(user);
                    }
                    progressBar.setVisibility(View.GONE);
                    userAdapter.setUserList(this, userList);
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
            }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UsersListActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}