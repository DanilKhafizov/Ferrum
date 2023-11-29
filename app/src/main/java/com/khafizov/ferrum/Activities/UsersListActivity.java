package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;

import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.UserCardView;

import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

    }
    UserCardView cardView = findViewById(R.id.user_card_view);
    AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase")
            .build();
    UserDao userDao = appDatabase.userDao();
    List<User> users = userDao.getAllUsers();
    int userIndex = 0;
    int size = users.size();
    UserCardView userCardView = new UserCardView(context);

    while (userIndex < users.size() {
        User user = users.get(userIndex);
        userCardView.setUserInfo(user);
        userIndex++;
    }
//        appDatabase.close();
}