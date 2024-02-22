package com.khafizov.ferrum.Registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.Activities.TrainersActivity;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private CheckBox agreeCheckbox;
    private ImageButton showPasswordBtn;

    TextInputEditText editTextName, editTextSurname, editTextEmail, editTextPassword;
    MaterialButton signUpBtn;
    ProgressBar progressBar;
    TextView termsTv;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        editTextName = findViewById(R.id.input_name);
        editTextSurname = findViewById(R.id.input_surname);
        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        signUpBtn = findViewById(R.id.SignUpBtn);
        progressBar = findViewById(R.id.progressBar);
        termsTv = findViewById(R.id.terms_tv);
        agreeCheckbox = findViewById(R.id.agreeCheckbox);
        showPasswordBtn = findViewById(R.id.show_password_btn);
        ImageButton backButton = findViewById(R.id.back_btn);

        showPasswordBtn.setOnClickListener(v -> {
            // Логика для показа/скрытия пароля

            int inputType = editTextPassword.getInputType();

            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_hide); // Изменить иконку на показать пароль
            } else {
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_show); // Изменить иконку на скрыть пароль
            }

            // Установить курсор в конец текста
            editTextPassword.setSelection(editTextPassword.getText().length());
        });

        backButton.setOnClickListener(v -> showWelcomeActivity());

        signUpBtn.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            registerUser(name, surname, email, password);

        });

        String termsOfUseText = getResources().getString(R.string.terms_of_use_text);
        String personalInformationText = getResources().getString(R.string.personal_information_text);
        String fullText = getString(R.string.accept_terms_text);
        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf(termsOfUseText);
        int endIndex = startIndex + termsOfUseText.length();
        int startIndex1 = fullText.indexOf(personalInformationText);
        int endIndex1 = startIndex1 + personalInformationText.length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Открываем страницу с пользовательским соглашением во встроенном браузере
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/terms"));
                startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsTv.setText(spannableString);
        termsTv.setMovementMethod(LinkMovementMethod.getInstance());

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Открываем страницу с пользовательским соглашением во встроенном браузере
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://assistentus.ru/forma/soglasie-na-obrabotku-personalnyh-dannyh/"));
                startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan1, startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsTv.setText(spannableString);
        termsTv.setMovementMethod(LinkMovementMethod.getInstance());

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
        if (agreeCheckbox.isChecked()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToDatabase(user.getUid());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegActivity.this, "Ошибка при получении текущего пользователя", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegActivity.this, "Пользователь с таким Email уже существует", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegActivity.this, "Проверьте правильность заполнения поля Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            showToast("Примите условия Пользовательского соглашения!");
        }
    }



    private void saveUserToDatabase(String userId) {
      clearUserData();
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        // Создание LinkedHashMap для сохранения данных в правильной последовательности
        Map<String, Object> user = new LinkedHashMap<>();
        user.put(Constants.KEY_ROLE, userEmail.equals("ferrumslv@gmail.com") ||
                userEmail.equals("danilhafizov123kk@gmail.com") ? "Администратор" : "Пользователь");
        user.put(Constants.KEY_NAME, editTextName.getText().toString());
        user.put(Constants.KEY_SURNAME, editTextSurname.getText().toString());
        user.put(Constants.KEY_EMAIL,editTextEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, editTextPassword.getText().toString());
        user.put(Constants.KEY_REGISTRATION_DATE, FieldValue.serverTimestamp());
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, userId);
                    preferenceManager.putString(Constants.KEY_NAME, editTextName.getText().toString());
                    preferenceManager.putString(Constants.KEY_SURNAME,editTextSurname.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL,editTextEmail.getText().toString());
                }) .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            signUpBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            signUpBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void showWelcomeActivity()
    {
        Intent intent = new Intent(RegActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearUserData() {
        preferenceManager.clearPreferences();
    }




    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }
}
