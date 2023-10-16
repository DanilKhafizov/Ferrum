package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;

import org.w3c.dom.Text;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton langBtn, themeBtn, styleBtn, backBtn;
    private Button editBtn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvSurname = findViewById(R.id.tv_surname);
        tvName = findViewById(R.id.tv_name);
        TextView = findViewById(R.id.textView);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        langBtn = findViewById(R.id.go_lang_im_btn);
        themeBtn = findViewById(R.id.go_theme_im_btn);
        styleBtn = findViewById(R.id.go_style_im_btn);
        editBtn = findViewById(R.id.edit_btn);
        backBtn = findViewById(R.id.back_btn);

        mAuth = FirebaseAuth.getInstance();



            loadAllUsers();
            loadUserProfile();


//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            String surname = user.getDisplayName();
//            String name = user.getDisplayName();
//            String birthday = user.getDisplayName();
//            String email = user.getEmail();
//            String phone = user.getDisplayName();
//            tvSurname.setText(surname);
//            tvName.setText(name);
//            tvBirthday.setText(birthday);
//            tvEmail.setText(email);
//            tvPhone.setText(phone);
//        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        langBtn.setOnClickListener(v -> showSettingsActivity() );
        themeBtn.setOnClickListener(v -> showSettingsActivity() );
        styleBtn.setOnClickListener(v -> showSettingsActivity() );
        editBtn.setOnClickListener(v -> showEditProfileActivity() );
        backBtn.setOnClickListener(v -> showMainActivity() );

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
                    StringBuilder usersText = new StringBuilder();
                    for (User user : userList) {
                        usersText.append(user.getName()).append(" ").append(user.getSurname()).append("\n");
                    }
                    TextView.setText(usersText.toString());
                }
            }
        };

        loadUsersTask.execute();
    }
    private void loadUserProfile() {
        AsyncTask<Void, Void, User> loadUserTask = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
                UserDao userDao = appDatabase.userDao();
                return userDao.getLastRegisteredUser();
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    tvName.setText(user.getName());
                    tvSurname.setText(user.getSurname());
                    tvEmail.setText(user.getEmail());
                }
            }
        };

        loadUserTask.execute();
    }
    public void showSettingsActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void showEditProfileActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMainActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}