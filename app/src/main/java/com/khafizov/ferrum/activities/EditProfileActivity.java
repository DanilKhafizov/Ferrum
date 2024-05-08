package com.khafizov.ferrum.activities;

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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.ImageEncoder;
import com.khafizov.ferrum.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EditProfileActivity extends AppCompatActivity {


    private PreferenceManager preferenceManager;
    private Boolean IsOriginalBitmap = false;
    private Bitmap originalBitmap;
    private Bitmap bitmapImage;
    private int currentRotationAngle = 0;
    private EditText newName, newSurname, newBirthday, newPhone;
    private RoundedImageView userImage;
    private ImageButton backBtn;
    private AppCompatImageButton rotateBtn;
    private Button editImageBtn, saveBtn, cancelBtn;
    private String name, surname, birthday, phone, pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        listeners();

    }

    private void init(){
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
    }

    private void listeners(){
        displayUserInfo();
        backBtn.setOnClickListener(v -> showProfileActivity() );
        cancelBtn.setOnClickListener(v -> showProfileActivity() );
        rotateBtn.setOnClickListener(v -> rotateImage() );
        editImageBtn.setOnClickListener(v ->  showImageOptionsDialog() );
        newBirthday.setOnClickListener(v -> showDatePickerDialog());
        saveBtn.setOnClickListener(v -> {
            name = newName.getText().toString();
            surname = newSurname.getText().toString();
            birthday = newBirthday.getText().toString();
            phone = newPhone.getText().toString();
            String image = preferenceManager.getString(Constants.KEY_IMAGE);
            if (name != null && !name.isEmpty()) {
                preferenceManager.putString(Constants.KEY_NAME, name);}
            if (surname != null && !surname.isEmpty()) {
                preferenceManager.putString(Constants.KEY_SURNAME, surname);}
            if (birthday != null && !birthday.isEmpty()) {
                preferenceManager.putString(Constants.KEY_BIRTHDAY, birthday);}
            if (phone != null && !phone.isEmpty()) {
                preferenceManager.putString(Constants.KEY_PHONE, phone);}
            if (image != null && !image.isEmpty()) {
                preferenceManager.putString(Constants.KEY_IMAGE, pic);}
            else{
                preferenceManager.putString(Constants.KEY_IMAGE, pic);}
            updateUserInfo();
            showProfileActivity();});}

    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Что вы хотите сделать?");
        builder.setItems(new CharSequence[]{"Изменить фото", "Удалить фото"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            pickImage();
                            break;
                        case 1:
                            deleteImage();
                            break;
                    }
                });
        builder.show();
    }

    private void deleteImage() {
        pic = null;
        userImage.setImageResource(android.R.color.transparent);
        rotateBtn.setVisibility(View.INVISIBLE);
        showToast("Фото удалено");
    }
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private void rotateImage() {
        ImageEncoder imageEncoder = new ImageEncoder();
        currentRotationAngle += 90;
        if (IsOriginalBitmap){
            Bitmap rotatedBitmap = rotateBitmap(originalBitmap, currentRotationAngle);
            userImage.setImageBitmap(rotatedBitmap);
            pic = imageEncoder.encodeImage(rotatedBitmap, 125, 125);
        }
        if (!IsOriginalBitmap){
            if (bitmapImage == null && originalBitmap == null){
                showToast("Добавьте изображение");
            }
            else{
                Bitmap rotatedBitmap = rotateBitmap(bitmapImage, currentRotationAngle);
                userImage.setImageBitmap(rotatedBitmap);
                pic = imageEncoder.encodeImage(rotatedBitmap, 125, 125);
            }

        }

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
                            userImage.setImageBitmap(originalBitmap);
                            ImageEncoder imageEncoder = new ImageEncoder();
                            pic = imageEncoder.encodeImage(originalBitmap, 125, 125);
                            if (pic != null){
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
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
        pic = preferenceManager.getString(Constants.KEY_IMAGE);
        newName.setText(name);
        newSurname.setText(surname);
        newBirthday.setText(birthday);
        newPhone.setText(phone);

        if (pic != null && !pic.isEmpty()) {
            bitmapImage = decodeImage(pic);
            userImage.setImageBitmap(bitmapImage);
            rotateBtn.setVisibility(View.VISIBLE);
        }
        else{
            rotateBtn.setVisibility(View.INVISIBLE);
        }
    }


    public void updateUserInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String name = preferenceManager.getString(Constants.KEY_NAME);
        String surname = preferenceManager.getString(Constants.KEY_SURNAME);
        String birthday = preferenceManager.getString(Constants.KEY_BIRTHDAY);
        String phone = preferenceManager.getString(Constants.KEY_PHONE);
        String image = preferenceManager.getString(Constants.KEY_IMAGE);
        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_NAME, name);
        updates.put(Constants.KEY_SURNAME, surname);
        updates.put(Constants.KEY_BIRTHDAY, birthday);
        updates.put(Constants.KEY_PHONE, phone);
        updates.put(Constants.KEY_IMAGE, image);
        db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> showToast("Информация успешно обновлена"))
                .addOnFailureListener(e -> showToast("Ошибка при обновлении: " + e.getMessage()));}

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