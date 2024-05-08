package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.ImageEncoder;
import com.khafizov.ferrum.utilities.PreferenceManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.santalu.maskara.Mask;
import com.santalu.maskara.MaskChangedListener;
import com.santalu.maskara.widget.MaskEditText;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private Bitmap decodedBitmap;
    private Bitmap originalBitmap;
    public TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton backBtn, birthdayAdd, phoneAdd, signOutBtn;
    private RelativeLayout feedBackBtn, callClubBtn, vkBtn, adminBtn;
    private RoundedImageView userImageBtn;
    private Button editBtn;
    private AppCompatImageButton rotateBtn;
    private PreferenceManager preferenceManager;
    private String pic;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        listeners();
    }

    private void init(){
        tvSurname = findViewById(R.id.surname_tv);
        tvName = findViewById(R.id.name_tv);
        TextView = findViewById(R.id.textView);
        tvBirthday = findViewById(R.id.birthday_tv);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.phone_tv);
        feedBackBtn = findViewById(R.id.feed_back_im_btn);
        callClubBtn = findViewById(R.id.call_club_im_btn);
        vkBtn = findViewById(R.id.vk_im_btn);
        editBtn = findViewById(R.id.edit_btn);
        backBtn = findViewById(R.id.back_btn);
        birthdayAdd = findViewById(R.id.birthday_add_im_btn);
        phoneAdd = findViewById(R.id.phone_add_im_btn);
        signOutBtn = findViewById(R.id.signOutBtn);
        rotateBtn = findViewById(R.id.rotateButton);
        adminBtn = findViewById(R.id.tools_im_btn);
        userImageBtn = findViewById(R.id.user_im_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());
        bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
    }
    @SuppressLint("NonConstantResourceId")
    private void listeners(){
        getUserDataFromFirestore();
        adminListeners();
        birthdayAdd.setOnClickListener(v -> {
            showDatePickerDialog();
            birthdayAdd.setVisibility(View.GONE);
        });
        rotateBtn.setOnClickListener(v -> rotateImage());
        adminBtn.setOnClickListener(v -> createForm());
        userImageBtn.setOnClickListener(v -> {
            if (decodedBitmap == null) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
        signOutBtn.setOnClickListener(v -> logout());
        phoneAdd.setOnClickListener(v -> showPhoneNumberDialog());
        feedBackBtn.setOnClickListener(v -> showFeedBackActivity());
        callClubBtn.setOnClickListener(v -> callClub());
        vkBtn.setOnClickListener(v -> openVKPage());
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
    private void adminListeners(){
        String role = preferenceManager.getString(Constants.KEY_ROLE);
        if(role.equals("Администратор")){
            adminBtn.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        String birthdayText = tvBirthday.getText().toString();
        String phoneText = tvPhone.getText().toString();
        if (!birthdayText.equals(getResources().getString(R.string.birthday_text))) {
            birthdayAdd.setVisibility(View.GONE);
        } else {
            birthdayAdd.setVisibility(View.VISIBLE);
        }
        if (!phoneText.equals(getResources().getString(R.string.phone_text))) {
            phoneAdd.setVisibility(View.GONE);
        } else {
            phoneAdd.setVisibility(View.VISIBLE);
        }
    }
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private void rotateImage() {
        int currentRotationAngle = 90;
        originalBitmap = rotateBitmap(originalBitmap, currentRotationAngle);
        userImageBtn.setImageBitmap(originalBitmap);
        ImageEncoder imageEncoder = new ImageEncoder();
        pic = imageEncoder.encodeImage(originalBitmap, 125, 125);
        saveImageToFirestore(pic);
    }
    private void saveImageToFirestore(String image) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .update(Constants.KEY_IMAGE, image)
                .addOnSuccessListener(aVoid -> {
                    preferenceManager.putString(Constants.KEY_IMAGE, image);
                    showToast("Изображение успешно обновлено");
                })
                .addOnFailureListener(e -> showToast("Ошибка при обновлении изображения: " + e.getMessage()));
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
                            userImageBtn.setImageBitmap(originalBitmap);
                            ImageEncoder imageEncoder = new ImageEncoder();
                            pic = imageEncoder.encodeImage(originalBitmap, 125, 125);
                            saveImageToFirestore(pic);
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
            tvName.setText(name);}
        if (surname != null && !surname.isEmpty()) {
            tvSurname.setText(surname);}
        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);}
        if (birthday != null && !birthday.isEmpty()) {
            tvBirthday.setText(birthday);}
        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText(phone);}
        if (image != null && !image.isEmpty()) {
            decodedBitmap = getImage(image);
            userImageBtn.setImageBitmap(decodedBitmap);}}


    private void showPhoneNumberDialog(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Введите номер телефона");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_phone_number, null);
        MaskEditText phoneEd = dialogView.findViewById(R.id.phone_ed);

        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String phoneNumber = phoneEd.getMasked();
            if (!TextUtils.isEmpty(phoneNumber)) {
                preferenceManager.putString(Constants.KEY_PHONE, phoneNumber);
                tvPhone.setText(phoneNumber);
                phoneAdd.setVisibility(View.GONE);
            }
            db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                    .update(Constants.KEY_PHONE, phoneNumber)
                    .addOnSuccessListener(aVoid -> showToast("Номер телефона обновлен успешно"))
                    .addOnFailureListener(e -> showToast("Ошибка при обновлении номера телефона: " + e.getMessage()));
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showDatePickerDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvBirthday.setText(selectedDate);
            db.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                    .update(Constants.KEY_BIRTHDAY, selectedDate)
                    .addOnSuccessListener(aVoid -> {
                            preferenceManager.putString(Constants.KEY_BIRTHDAY, selectedDate);
                            showToast("Дата рождения обновлена успешно");
                    })
                    .addOnFailureListener(e -> showToast("Ошибка при обновлении даты рождения: " + e.getMessage()));
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this, dateSetListener, 2022, 0, 1);
        datePickerDialog.setOnCancelListener(dialog -> birthdayAdd.setVisibility(View.VISIBLE));
        datePickerDialog.show();
    }

    private static Bitmap getImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Установка конфигурации для сохранения качества
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
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


    public void createForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите, что хотите сделать");
        builder.setPositiveButton("Добавить услугу", (dialog, which) -> showAddServiceActivity());
        builder.setNegativeButton("Добавить сотрудника", (dialog, which) -> showAddTrainerActivity());
        builder.setNeutralButton("Открыть список пользователей", (dialog, which) -> showUsersListActivity());
        builder.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void callClub(){
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы хотите позвонить в фитнес-клуб?")
                .setPositiveButton("Да", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String club_phone = "89876233456";
                    intent.setData(Uri.parse("tel:" + club_phone));
                    // Проверка наличия приложения для звонков на устройстве
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        showToast("Приложения для звонков не найдено");
                    }
                })
                .setNegativeButton("Нет", (dialog, which) -> {})
                .show();

    }

    private void openVKPage(){
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Открыть приложение ВКонтакте?")
                .setPositiveButton("Да", (dialog, which) -> {
                    String vk = "https://vk.com/ferrumslv?w=club115160325";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vk));
                    startActivity(intent);
                })
                .setNegativeButton("Нет", (dialog, which) -> {})
                .show();
    }


    public void showAddServiceActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, AddServiceActivity.class);
        startActivity(intent);
        finish();
    }


    public void showAddTrainerActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, TrainersActivity.class);
        startActivity(intent);
        finish();
    }


    public void showFeedBackActivity()
    {
        Intent intent = new Intent(ProfileActivity.this, FeedBackActivity.class);
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