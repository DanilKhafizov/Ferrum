package com.khafizov.ferrum.Registration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.EmployeeDao;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;


import java.util.List;

public class RegActivity extends AppCompatActivity {

    TextInputEditText editTextName, editTextSurname, editTextEmail, editTextPassword;
    ImageButton registerBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.input_name);
        editTextSurname = findViewById(R.id.input_surname);
        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        registerBtn = findViewById(R.id.reg_img_btn);
        registerBtn.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            registerUser(name, surname, email, password);
        });
    }

    private void registerUser(String name, String surname, String email, String password) {

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(RegActivity.this, "Пароль должен содержать больше 6 символов!", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(name, surname, email);
                            startActivity(new Intent(RegActivity.this, MainActivity.class));
                            finish();
                        }
                        SharedPreferences settings = getSharedPreferences("AppSettings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("IsFirstRun", true);
                        editor.apply();
                    } else {
                        Toast.makeText(RegActivity.this, "Отказано в регистрации. Заполните все поля и проверьте правильность заполнения блоков!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }



    private void saveUserToDatabase(String name, String surname, String email) {

        AsyncTask<Void, Void, Void> saveUserTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Инициализация базы данных и получение соответствующего DAO
                AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
                UserDao userDao = appDatabase.userDao();

                // Создание объекта пользователя
                User user = new User();
                user.setName(name);
                user.setSurname(surname);
                user.setEmail(email);

                // Сохранение пользователя в базе данных
                userDao.insert(user);
                Log.d("SaveUser", "Name: " + user.getName());
                Log.d("SaveUser", "Surname: " + user.getSurname());
                Log.d("SaveUser", "Email: " + user.getEmail());
                Log.d("SaveUser", "Id: " + user.getId());

                return null;
            }
        };
        saveUserTask.execute();
    }


    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }
}
