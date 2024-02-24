package com.khafizov.ferrum.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity {


    private PreferenceManager preferenceManager;
    private Boolean IsOriginalBitmap = false;
    private Bitmap rotatedBitmap;
    private Bitmap originalBitmap; // Переменная для хранения оригинального изображения
    private Bitmap bitmap; // Переменная для текущего изображения
    private Bitmap bitmapImage;
    private int currentRotationAngle = 0; // Текущий угол поворота
    private String encodedImage;
    private EditText newName, newSurname, newBirthday, newPhone;
    private RoundedImageView userImage;
    private ImageButton backBtn;
    private AppCompatImageButton rotateBtn;
    private Button editImageBtn, saveBtn, cancelBtn;
    private FirebaseAuth mAuth;
    private String name, surname, birthday, phone, image;

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
        cancelBtn = findViewById(R.id.cancel_btn);
        rotateBtn = findViewById(R.id.rotateButton);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(v -> showProfileActivity() );
        cancelBtn.setOnClickListener(v -> showProfileActivity() );

        rotateBtn.setOnClickListener(v -> rotateImage() );
        editImageBtn.setOnClickListener(v ->  showImageOptionsDialog() );

        newBirthday.setOnClickListener(v -> showDatePickerDialog());

       displayUserInfo();
       showToast("encodedImage = " + encodedImage);

        saveBtn.setOnClickListener(v -> {
            name = newName.getText().toString();
            surname = newSurname.getText().toString();
            birthday = newBirthday.getText().toString();
            phone = newPhone.getText().toString();
            String image = preferenceManager.getString(Constants.KEY_IMAGE);
            if (name != null && !name.isEmpty()) {
                preferenceManager.putString(Constants.KEY_NAME, name);
            }
            if (surname != null && !surname.isEmpty()) {
                preferenceManager.putString(Constants.KEY_SURNAME, surname);
            }
            if (birthday != null && !birthday.isEmpty()) {
                preferenceManager.putString(Constants.KEY_BIRTHDAY, birthday);
            }
            if (phone != null && !phone.isEmpty()) {
                preferenceManager.putString(Constants.KEY_PHONE, phone);
            }
            if (image != null && !image.isEmpty()) {
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
            }
            else{
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
            }

            updateUserInfo();
           //updateProfileImage(encodedImage);
            showProfileActivity();
        });
    }

    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Что вы хотите сделать?");
        builder.setItems(new CharSequence[]{"Изменить фото", "Удалить фото"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImage(); // Выбрать новое изображение из галереи
                            break;
                        case 1:
                            deleteImage(); // Удалить текущее изображение
                            break;
                    }
                });
        builder.show();
    }

    private void deleteImage() {
        // Удаление текущего изображения
        encodedImage = null;
        userImage.setImageResource(android.R.color.transparent); // Очистить изображение
        rotateBtn.setVisibility(View.INVISIBLE);
        showToast("Фото удалено");
        // Дополнительные действия по удалению изображения, если необходимо
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void rotateImage() {
        // Увеличиваем текущий угол поворота на 90 градусов
        currentRotationAngle += 90;
        // Поворачиваем изображение на 90 градусов
        //preferenceManager.putString(Constants.KEY_IMAGE, );
        if (IsOriginalBitmap == true){
            //showToast("originalBitmap");
            Bitmap rotatedBitmap = rotateBitmap(originalBitmap, currentRotationAngle);
            // Отображаем повернутое изображение
            userImage.setImageBitmap(rotatedBitmap);
            // Обновляем закодированное изображение
            encodedImage = encodeImage(rotatedBitmap);
        }
        if (IsOriginalBitmap == false){
            //showToast("bitmapImage");
            if (bitmapImage == null && originalBitmap == null){
                showToast("Добавьте изображение");
            }
            else{
                Bitmap rotatedBitmap = rotateBitmap(bitmapImage, currentRotationAngle);
                // Отображаем повернутое изображение
                userImage.setImageBitmap(rotatedBitmap);
                // Обновляем закодированное изображение
                encodedImage = encodeImage(rotatedBitmap);
            }

        }

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

    private void pickImage() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
            IsOriginalBitmap = true;



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
                            userImage.setImageBitmap(originalBitmap);
                            // Кодирование изображения
                            encodedImage = encodeImage(originalBitmap);

                            if (encodedImage != null){
                                rotateBtn.setVisibility(View.VISIBLE);
                            }


                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );



    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            newBirthday.setText(selectedDate);
            preferenceManager.putString(Constants.KEY_BIRTHDAY, selectedDate);
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, dateSetListener, 2022, 0, 1);
        datePickerDialog.show();
    }

    public void displayUserInfo() {
        // Получаем информацию о пользователе из SharedPreferences
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
        encodedImage = preferenceManager.getString(Constants.KEY_IMAGE);
        // Отображаем информацию пользователя в EditText
        newName.setText(name);
        newSurname.setText(surname);
        newBirthday.setText(birthday);
        newPhone.setText(phone);

        if (encodedImage != null && !encodedImage.isEmpty()) {
            bitmapImage = decodeImage(encodedImage); // Декодируем изображение из строки Base64
            userImage.setImageBitmap(bitmapImage); // Устанавливаем изображение в ImageView
            rotateBtn.setVisibility(View.VISIBLE);
        }
        else{
            rotateBtn.setVisibility(View.INVISIBLE);
        }
    }
//    public void updateProfileImage(String encodedImage) {
//        // Здесь можно выполнить загрузку нового изображения пользователя в Firebase Storage, если это необходимо
//        // Обновляем информацию о изображении пользователя в SharedPreferences
//        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
//        // Обновляем информацию о изображении пользователя в Firestore
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
//                .update("image", encodedImage)
//                .addOnSuccessListener(aVoid -> showToast("Изображение профиля успешно обновлено"))
//                .addOnFailureListener(e -> showToast("Ошибка при обновлении изображения профиля: " + e.getMessage()));
//    }

    public void updateUserInfo() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
        String image = preferenceManager.getString(Constants.KEY_IMAGE);

        // Обновляем информацию в Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("surname", surname);
        updates.put("birthday", birthday);
        updates.put("phone", phone);
        updates.put("image", image);

        db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> showToast("Информация успешно обновлена"))
                .addOnFailureListener(e -> showToast("Ошибка при обновлении: " + e.getMessage()));


        }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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