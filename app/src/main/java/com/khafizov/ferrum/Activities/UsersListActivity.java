package com.khafizov.ferrum.Activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
        // Подключение к базе данных Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Получение ссылки на коллекцию "users"
        CollectionReference usersRef = db.collection("users");

// Загрузка списка всех пользователей
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class); // Предположим, что у вас есть класс User для модели пользователей
                    userList.add(user);
                }
                userAdapter.setUserList(userList);
                userAdapter.notifyDataSetChanged(); // Обновление списка после загрузки данных
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UsersListActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}