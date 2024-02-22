package com.khafizov.ferrum.Activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khafizov.ferrum.Database.UserAdapter;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.User;

import java.util.ArrayList;
import java.util.List;


public class UsersListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        setupRecyclerView();
        loadAllUsers();
    }
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        recyclerView.setAdapter(userAdapter);
    }
    private void loadAllUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .orderBy("registrationDate", Query.Direction.DESCENDING) // Сортировка по полю "registrationDate" по возрастанию
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    userAdapter.setUserList(userList);
                    userAdapter.notifyDataSetChanged(); // Обновление списка после загрузки данных
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