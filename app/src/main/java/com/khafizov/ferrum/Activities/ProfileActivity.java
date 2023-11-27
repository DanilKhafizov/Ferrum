package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton langBtn, themeBtn, styleBtn, backBtn, birthdayAdd, phoneAdd;
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
        birthdayAdd = findViewById(R.id.birthday_add_im_btn);
        phoneAdd = findViewById(R.id.phone_add_im_btn);

        mAuth = FirebaseAuth.getInstance();


            loadUserProfile();
            loadAllUsers();



   //     FirebaseUser user = mAuth.getCurrentUser();
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

        birthdayAdd.setOnClickListener(v -> showDatePickerDialog());

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
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private void loadUserProfile() {
        AsyncTask<Void, Void, User> loadUserTask = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
                UserDao userDao = appDatabase.userDao();

                // Получаем идентификатор текущего пользователя из Firebase Authentication
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userEmail = currentUser.getEmail();

                // Получаем пользователя из Room с учетом идентификатора
                return userDao.getUserByEmail(userEmail);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    // Устанавливаем значения в EditText
                    tvName.setText(user.getName());
                    tvSurname.setText(user.getSurname());

                    // Если у вас есть доступ к объекту FirebaseUser, вы можете использовать его для получения почты пользователя

                    if (currentUser != null) {
                        tvEmail.setText(currentUser.getEmail());
                    }
                }
            }
        };

        loadUserTask.execute();
    }

    private void showDatePickerDialog() {
        // Получите текущую дату для установки значения по умолчанию в календаре
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Создайте слушатель для обработки выбора даты
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year1, month1, dayOfMonth) -> {
            // Преобразуйте выбранную дату в строку в нужном формате
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);

            // Получите экземпляр базы данных Room
//            AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());

            // Получите текущего пользователя из базы данных

//                User user = appDatabase.userDao().getCurrentUser();

            // Обновите поле "dateOfBirth" в объекте пользователя
//            user.setBirthday(selectedDate);

            // Сохраните изменения в базе данных Room
//            appDatabase.userDao().updateUser(user);
        };

        // Создайте диалоговое окно календаря и установите слушатель выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
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