package com.khafizov.ferrum.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.Registration.WelcomeActivity;
import com.khafizov.ferrum.models.User;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private Bitmap decodedBitmap;
    private Bitmap originalBitmap; // Переменная для хранения оригинального изображения
    private Bitmap bitmap; // Переменная для текущего изображения
    private int currentRotationAngle = 0; // Текущий угол поворота
    public static TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton langBtn, themeBtn, styleBtn, backBtn, birthdayAdd, phoneAdd, signOutBtn;
    private RoundedImageView userImageBtn;
    private Button editBtn, loadUsersBtn;
    private AppCompatImageButton rotateBtn;
    private PreferenceManager preferenceManager;
    private String encodedImage;
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
        langBtn = findViewById(R.id.go_user_guide);
        themeBtn = findViewById(R.id.go_terms_of_use);
        styleBtn = findViewById(R.id.go_club_rules);
        editBtn = findViewById(R.id.edit_btn);
        backBtn = findViewById(R.id.back_btn);
        birthdayAdd = findViewById(R.id.birthday_add_im_btn);
        phoneAdd = findViewById(R.id.phone_add_im_btn);
        loadUsersBtn = findViewById(R.id.load_users_btn);
        signOutBtn = findViewById(R.id.signOutBtn);
        rotateBtn = findViewById(R.id.rotateButton);
        userImageBtn = findViewById(R.id.user_im_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUserDataFromFirestore();

        loadUsersBtn.setOnClickListener(v -> showUsersListActivity());

        birthdayAdd.setOnClickListener(v -> {
            showDatePickerDialog();
            birthdayAdd.setVisibility(View.GONE);
        });

        rotateBtn.setOnClickListener(v -> rotateImage());

        userImageBtn.setOnClickListener(v -> {
            if (decodedBitmap == null) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
            else{
                return;
            }
        });



        signOutBtn.setOnClickListener(v -> logout());

        phoneAdd.setOnClickListener(v -> showPhoneNumberDialog());

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        langBtn.setOnClickListener(v ->  showSettingsActivity());
        themeBtn.setOnClickListener(v -> showSettingsActivity());
        styleBtn.setOnClickListener(v -> showSettingsActivity());

        editBtn.setOnClickListener(v -> showEditProfileActivity());

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

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void rotateImage() {
        // Увеличиваем текущий угол поворота на 90 градусов
        currentRotationAngle = 90;
        // Поворачиваем изображение на 90 градусов
        bitmap = rotateBitmap(bitmap, currentRotationAngle);
        // Отображаем повернутое изображение
        userImageBtn.setImageBitmap(bitmap);
        // Обновляем закодированное изображение
        encodedImage = encodeImage(bitmap);

        // Сохраняем закодированное изображение в Firestore
        saveImageToFirestore(encodedImage);
    }

    private void saveImageToFirestore(String encodedImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Обновляем информацию в базе данных
        db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .update("image", encodedImage)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    showToast("Изображение пользователя успешно обновлено в Firestore");
                })
                .addOnFailureListener(e -> showToast("Ошибка при обновлении изображения пользователя в Firestore: " + e.getMessage()));
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            originalBitmap = BitmapFactory.decodeStream(inputStream);
                            bitmap = originalBitmap; // Сохраняем оригинальное изображение
                            // Отображаем оригинальное изображение
                            userImageBtn.setImageBitmap(originalBitmap);
                            // Кодирование изображения
                            encodedImage = encodeImage(originalBitmap);
                            // Сохраняем закодированное изображение в Firestore
                            saveImageToFirestore(encodedImage);
                            // Показываем кнопку поворота
                            rotateBtn.setVisibility(View.VISIBLE);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void getUserDataFromFirestore() {
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
        String image = preferenceManager.getString(Constants.KEY_IMAGE);


        if (name != null && !name.isEmpty()) {
            tvName.setText(name);
        }
        if (surname != null && !surname.isEmpty()) {
            tvSurname.setText(surname);
        }
        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
        }
        if (birthday != null && !birthday.isEmpty()) {
            tvBirthday.setText(birthday);
        }
        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText(phone);
        }

        if (image != null && !image.isEmpty()) {
            decodedBitmap = decodeImage(image);
            userImageBtn.setImageBitmap(decodedBitmap);
//            User user = new User();
//            String imageURL = encodedImage;
//            user.setPhoto(imageURL);
        }

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
                preferenceManager.putString(Constants.KEY_PHONE, phoneNumber);
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
                    .addOnSuccessListener(aVoid -> {
                            preferenceManager.putString(Constants.KEY_BIRTHDAY, selectedDate);
                            showToast("Дата рождения обновлена успешно");
                    })
                    .addOnFailureListener(e -> showToast("Ошибка при обновлении даты рождения: " + e.getMessage()));
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, dateSetListener, 2022, 0, 1);
        datePickerDialog.setOnCancelListener(dialog -> {
            birthdayAdd.setVisibility(View.VISIBLE); // Показать кнопку "+"
        });
        datePickerDialog.show();
    }




    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                .setPositiveButton("Да", (dialog, which) -> {
                    showToast("Выход из аккаунта...");
                    FirebaseAuth.getInstance().signOut();
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    finish();
                })
                .setNegativeButton("Нет", (dialog, which) -> {})
                .show();
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