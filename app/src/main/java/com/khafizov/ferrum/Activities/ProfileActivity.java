package com.khafizov.ferrum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khafizov.ferrum.Database.AppDatabase;
import com.khafizov.ferrum.Database.ImageUtils;
import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.Database.UserDao;
import com.khafizov.ferrum.R;

import org.jetbrains.annotations.Nullable;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE_PICK = 1;
    private static final int MAX_WIDTH = 320; // Задайте желаемую максимальную ширину
    private static final int MAX_HEIGHT = 320; // Задайте желаемую максимальную высоту
//   private Bitmap defaultImageBitmap;

    public static TextView tvSurname, tvName, tvBirthday, tvEmail, tvPhone, TextView;
    private ImageButton langBtn, themeBtn, styleBtn, backBtn, birthdayAdd, phoneAdd, userImageBtn;
    private Button editBtn, loadUsersBtn;
    private FirebaseAuth mAuth;
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


            loadUserProfile();

//        defaultImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);



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

        loadUsersBtn.setOnClickListener(v -> showUsersListActivity());

       birthdayAdd.setOnClickListener(v -> {
           showDatePickerDialog();
           birthdayAdd.setVisibility(View.GONE);
    });

        phoneAdd.setOnClickListener(v -> {
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
                // Сохранить значение в БД Room
                String userEmail = mAuth.getCurrentUser().getEmail();
                updateUserInfo(userEmail, null, phoneNumber, null);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        userImageBtn.setOnClickListener(v -> {
//            int width = defaultImageBitmap.getWidth(); // Получите ширину изображения
//            int height = defaultImageBitmap.getHeight(); // Получите высоту изображения
//            Log.d("ImageSize", "Ширина: " + width + ", Высота: " + height);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String photoPath = getRealPathFromURI(selectedImageUri);
            if (photoPath != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(photoPath)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Изменяем размер фотографии
                                Bitmap resizedBitmap = resizeBitmap(resource, MAX_WIDTH, MAX_HEIGHT);

                                // Обрезаем фотографию в виде круга
                                Bitmap croppedBitmap = ImageUtils.getCircularBitmap(resizedBitmap);

                                // Устанавливаем обрезанную фотографию в ImageButton
                                userImageBtn.setImageBitmap(croppedBitmap);

                                // Сохраняем путь к изображению в базе данных
                                new Thread(() -> {
                                    AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
                                    UserDao userDao = appDatabase.userDao();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String userEmail = currentUser.getEmail();
                                    User user = userDao.getUserByEmail(userEmail);
                                    user.setPhotoUrl(photoPath);
//                                    user.setPhotoCropped(true); // Устанавливаем флаг обрезки фотографии
                                    updateUserInfo(userEmail, null, null, photoPath); // Передайте путь к изображению в метод
                                    userDao.updateUser(user);
                                    Log.d("Image", "photoPath = " + photoPath);
                                    Log.d("SaveUser", "Фото пользователя: " + user.getPhotoUrl());
                                }).start();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Placeholder callback
                            }
                        });
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Проверяем, что maxWidth и maxHeight больше 0
        if (maxWidth <= 0 || maxHeight <= 0) {
            // Если значения некорректны, возвращаем оригинальный битмап
            return bitmap;
        }
        // Вычисляем коэффициенты для изменения размера
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;

        // Используем минимальный коэффициент, чтобы сохранить пропорции
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        // Создаем матрицу преобразования для масштабирования
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        // Изменяем размер фотографии с помощью матрицы преобразования
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//            String birthdayText = tvBirthday.getText().toString();
            tvBirthday.setText(selectedDate);
//            if (!birthdayText.equals(getResources().getString(R.string.birthday_text))) {
                // Сохранить значение в БД Room
                String userEmail = mAuth.getCurrentUser().getEmail();
                updateUserInfo(userEmail, selectedDate, null, null);
//            }
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
//        if (photoPath != null) {
//            Glide.with(ProfileActivity.this)
//                    .load(photoPath)
//                    .into(userImageBtn);
//        }
    }


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
                    // Устанавливаем значения в TextView
                    tvName.setText(user.getName());
                    tvSurname.setText(user.getSurname());
                    if(user.getBirthday() != null)
                    {
                        tvBirthday.setText(user.getBirthday());
                        birthdayAdd.setVisibility(View.GONE); // Скрыть кнопку "+"
                    }
                    else{onResume();}
                    if(user.getPhone() != null)
                    {
                        tvPhone.setText(user.getPhone());
                        phoneAdd.setVisibility(View.GONE); // Скрыть кнопку "+"
                    }
                    else{onResume();}
                    // Если у вас есть доступ к объекту FirebaseUser, вы можете использовать его для получения почты пользователя
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        tvEmail.setText(currentUser.getEmail());
                    }
                    // Загрузка и отображение фотографии пользователя, если она доступна
                    if (user.getPhotoUrl() != null) {
//                        boolean isPhotoCropped = user.isPhotoCropped();
//                        if (isPhotoCropped) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getPhotoUrl())
                                .into(userImageBtn);
//                        }
                    }
                   else {
                            // Если фотография отсутствует, установите картинку по умолчанию
                            userImageBtn.setImageResource(R.drawable.default_image);
                        }





                }
            }
        };
        loadUserTask.execute();
    }

    public void updateUserInfo(String email, String birthday, String phone, String photoPath) {
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "FerrumDatabase").build();
            UserDao userDao = appDatabase.userDao();
            User user = userDao.getUserByEmail(email);
            if (user != null) {
                if (birthday != null) {
                    user.setBirthday(birthday);
                }
                if (phone != null) {
                    user.setPhone(phone);
                }
                if (photoPath != null) {
                    user.setPhotoUrl(photoPath); // Обновляем путь к изображению
                }
                userDao.updateUser(user);
            }

//            Log.d("SaveUser", "Name: " + user.getName());
//            Log.d("SaveUser", "Surname: " + user.getSurname());
//            Log.d("SaveUser", "Email: " + user.getEmail());
//            Log.d("SaveUser", "Birthday: " + user.getBirthday());
//            Log.d("SaveUser", "Phone: " + user.getPhone());

        });

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