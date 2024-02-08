package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserAdapter;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;

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
        AsyncTask<Void, Void, List<User>> loadUsersTask = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
                UserDao userDao = appDatabase.userDao();
                return userDao.getAllUsers();
            }
            @Override
            protected void onPostExecute(List<User> userList) {
                if (userList != null && !userList.isEmpty()) {
                    userAdapter.setUserList(userList);
                    userAdapter.notifyDataSetChanged(); // Обновление списка после загрузки данных
                }
            }
        };
        loadUsersTask.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UsersListActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}