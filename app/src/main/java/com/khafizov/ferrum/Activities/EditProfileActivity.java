package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.Activities.ProfileActivity;


public class EditProfileActivity extends AppCompatActivity {

    private EditText newName, newSurname, newBirthday, newPhone;
    private ImageView userImage;
    private ImageButton backBtn;
    private Button editImageBtn, saveBtn;
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        newName = findViewById(R.id.new_name_ed);
        newSurname = findViewById(R.id.new_surname_ed);
        newBirthday = findViewById(R.id.new_birthday_ed);
        newPhone = findViewById(R.id.new_phone_ed);
        userImage = findViewById(R.id.user_image);
        backBtn = findViewById(R.id.back_btn);
        editImageBtn = findViewById(R.id.edit_image_btn);
        saveBtn = findViewById(R.id.save_btn);
        mAuth = FirebaseAuth.getInstance();
        backBtn.setOnClickListener(v -> showProfileActivity() );

//        sharedPref();

//        editImageBtn.setOnClickListener(v -> {
//            openGallery();
//        });
//
        saveBtn.setOnClickListener(v -> {
            String name = newName.getText().toString();
            String surname = newSurname.getText().toString();
            String birthday = newBirthday.getText().toString();
            String phone = newPhone.getText().toString();

            // Сохраняем значения в SharedPreferences
//            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("name", name);
//            editor.putString("surname", surname);
//            editor.putString("birthday", birthday);
//            editor.putString("phone", phone);
//            editor.apply();

            String userEmail = mAuth.getCurrentUser().getEmail();
            updateUserInfo(userEmail, name, surname, birthday, phone);
            showProfileActivity();
        });

        newBirthday.setOnClickListener(v -> showDatePickerDialog());


    }


    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            newBirthday.setText(selectedDate);
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, dateSetListener, 2022, 0, 1);
        datePickerDialog.show();
    }

//    private void sharedPref(){
//
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String name = sharedPreferences.getString("name", "");
//        String surname = sharedPreferences.getString("surname", "");
//        String birthday = sharedPreferences.getString("birthday", "");
//        String phone = sharedPreferences.getString("phone", "");
//
//        // Установка значений в EditText в качестве текста-подсказки
//        newName.setText(name);
//        newSurname.setText(surname);
//        newBirthday.setText(birthday);
//        newPhone.setText(phone);
//    }

    public void updateUserInfo(String email, String name, String surname, String birthday, String phone) {
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
            UserDao userDao = appDatabase.userDao();
            User user = userDao.getUserByEmail(email);
            if (user != null) {
                if (name != null && !name.isEmpty()) {
                    user.setName(name);
                    newName.setText(user.getName());
                }
                if (surname != null && !surname.isEmpty()) {
                    user.setSurname(surname);
                    newSurname.setText(user.getSurname());
                }
                if (birthday != null && !birthday.isEmpty()) {
                    user.setBirthday(birthday);
                    newBirthday.setText(user.getBirthday());
                }
                if (phone != null && !phone.isEmpty()) {
                    user.setPhone(phone);
                    newPhone.setText(user.getPhone());
                }
                userDao.updateUser(user);
            }
            // Обновить элементы TextView только если значения не пустые
            if (name != null && !name.isEmpty()) {
                ProfileActivity.tvName.setText(name);
            }
            if (surname != null && !surname.isEmpty()) {
                ProfileActivity.tvSurname.setText(surname);
            }
            if (birthday != null && !birthday.isEmpty()) {
                ProfileActivity.tvBirthday.setText(birthday);
            }
            if (phone != null && !phone.isEmpty()) {
                ProfileActivity.tvPhone.setText(phone);
            }
        });
    }



    public void showProfileActivity()
    {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}