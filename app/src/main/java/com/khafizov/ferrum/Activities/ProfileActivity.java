package com.khafizov.ferrum.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {


    public static TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton langBtn, themeBtn, styleBtn, backBtn, birthdayAdd, phoneAdd, userImageBtn;
    private Button editBtn, loadUsersBtn;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvSurname = findViewById(R.id.surname_tv);
        tvName = findViewById(R.id.name_tv);
        TextView = findViewById(R.id.textView);
        tvBirthday = findViewById(R.id.birthday_tv);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.phone_tv);
        langBtn = findViewById(R.id.go_lang_im_btn);
        themeBtn = findViewById(R.id.go_theme_im_btn);
        styleBtn = findViewById(R.id.go_style_im_btn);
        editBtn = findViewById(R.id.edit_btn);
        backBtn = findViewById(R.id.back_btn);
        birthdayAdd = findViewById(R.id.birthday_add_im_btn);
        phoneAdd = findViewById(R.id.phone_add_im_btn);
        loadUsersBtn = findViewById(R.id.load_users_btn);
        userImageBtn = findViewById(R.id.user_im_btn);
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
       getUserDataFromFirestore();


        loadUsersBtn.setOnClickListener(v -> showUsersListActivity());

        birthdayAdd.setOnClickListener(v -> {
            showDatePickerDialog();
            birthdayAdd.setVisibility(View.GONE);
        });



        phoneAdd.setOnClickListener(v -> {
            showPhoneNumberDialog();

        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        langBtn.setOnClickListener(v ->  showSettingsActivity());
        themeBtn.setOnClickListener(v -> showSettingsActivity() );
        styleBtn.setOnClickListener(v -> showSettingsActivity() );
        editBtn.setOnClickListener(v -> {
            showEditProfileActivity();
        });

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
    private void showPhoneNumberDialog(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Введите номер телефона");
        final EditText input = new EditText(ProfileActivity.this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String phoneNumber = input.getText().toString();
            if (!TextUtils.isEmpty(phoneNumber)) {
                tvPhone.setText(phoneNumber);
                phoneAdd.setVisibility(View.GONE); // Скрыть кнопку "+"
            }
            // Обновление информации в базе данных
            db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                    .update("phone", phoneNumber)
                    .addOnSuccessListener(aVoid -> showToast("Номер телефона обновлен успешно"))
                    .addOnFailureListener(e -> showToast("Ошибка при обновлении номера телефона: " + e.getMessage()));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void showDatePickerDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvBirthday.setText(selectedDate);
            // Обновление информации в базе данных
            db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                    .update("birthday", selectedDate)
                    .addOnSuccessListener(aVoid -> showToast("Дата рождения обновлена успешно"))
                    .addOnFailureListener(e -> showToast("Ошибка при обновлении даты рождения: " + e.getMessage()));
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, dateSetListener, 2022, 0, 1);
        datePickerDialog.setOnCancelListener(dialog -> {
            birthdayAdd.setVisibility(View.VISIBLE); // Показать кнопку "+"
        });
        datePickerDialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Проверка даты рождения
        String birthdayText = tvBirthday.getText().toString();
        String phoneText = tvPhone.getText().toString();
        if (!birthdayText.equals(getResources().getString(R.string.birthday_text))) {
            birthdayAdd.setVisibility(View.GONE); // Скрыть кнопку "+"
        } else {
            birthdayAdd.setVisibility(View.VISIBLE); // Показать кнопку "+"
        }
        if (!phoneText.equals(getResources().getString(R.string.phone_text))) {
            phoneAdd.setVisibility(View.GONE); // Скрыть кнопку "+"
        } else {
            phoneAdd.setVisibility(View.VISIBLE); // Показать кнопку "+"
        }
    }

    private void getUserDataFromFirestore() {
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
                        // Обновление элементов TextView
                        if (birthday != null && !birthday.isEmpty()) {
                            tvBirthday.setText(birthday);
                        }
                        // Обновление элементов TextView
                        if (phone != null && !phone.isEmpty()) {
                            tvPhone.setText(phone);
                        }
                        tvName.setText(name);
                        tvSurname.setText(surname);
                        tvEmail.setText(email);
                    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showSettingsActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void showUsersListActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, UsersListActivity.class);
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