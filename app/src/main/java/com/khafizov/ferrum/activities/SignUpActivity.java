package com.khafizov.ferrum.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private CheckBox agreeCheckbox;
    private ImageButton showPasswordBtn, showConfirmPasswordBtn;
    private TextInputEditText editTextName, editTextSurname, editTextEmail, editTextPassword, editTextConfirmPassword;
    private MaterialButton signUpBtn;
    private TextView termsTv;
    private FirebaseAuth mAuth;
    private ImageButton backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        listeners();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        editTextName = findViewById(R.id.input_name);
        editTextSurname = findViewById(R.id.input_surname);
        editTextEmail = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        editTextConfirmPassword = findViewById(R.id.input_confirm_password);
        signUpBtn = findViewById(R.id.SignUpBtn);
        termsTv = findViewById(R.id.terms_tv);
        agreeCheckbox = findViewById(R.id.agreeCheckbox);
        showPasswordBtn = findViewById(R.id.show_password_btn);
        showConfirmPasswordBtn = findViewById(R.id.show_confirm_password_btn);
        backButton = findViewById(R.id.back_btn);
    }

    private void listeners(){
        backButton.setOnClickListener(v -> showWelcomeActivity());
        showPasswordBtn.setOnClickListener(v -> {
            int inputType = editTextPassword.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_hide);
            } else {
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordBtn.setImageResource(R.drawable.ic_show);
            }
            editTextPassword.setSelection(Objects.requireNonNull(editTextPassword.getText()).length());
        });

        showConfirmPasswordBtn.setOnClickListener(v -> {
            int inputType = editTextConfirmPassword.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showConfirmPasswordBtn.setImageResource(R.drawable.ic_hide);
            } else {
                editTextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showConfirmPasswordBtn.setImageResource(R.drawable.ic_show);
            }
            editTextConfirmPassword.setSelection(Objects.requireNonNull(editTextConfirmPassword.getText()).length());
        });

        signUpBtn.setOnClickListener(v -> {
            String name = Objects.requireNonNull(editTextName.getText()).toString();
            String surname = Objects.requireNonNull(editTextSurname.getText()).toString();
            String email = Objects.requireNonNull(editTextEmail.getText()).toString();
            String password = Objects.requireNonNull(editTextPassword.getText()).toString();
            String confirm_passoword = Objects.requireNonNull(editTextConfirmPassword.getText()).toString();
            registerUser(name, surname, email, password, confirm_passoword);

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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1XlxTGRuA2X6M2KdhgOMf4GSIdDc2YbnH/edit?usp=sharing&ouid=103884819035362832820&rtpof=true&sd=true"));
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
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1wHG-weYF-WGUIjVIldimVnronL1GLbXV/edit?usp=sharing&ouid=103884819035362832820&rtpof=true&sd=true"));
                startActivity(browserIntent);
            }
        };
        spannableString.setSpan(clickableSpan1, startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsTv.setText(spannableString);
        termsTv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void registerUser(String name, String surname, String email, String password, String confirm_password) {
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
            showToast("Все поля должны быть заполнены!");return;}
        if (password.length() < 6) {
            showToast("Пароль должен содержать больше 6 символов!");return;}
        if(!password.equals(confirm_password)) {
            showToast("Пароли не совпадают");return;}
        if (agreeCheckbox.isChecked()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
         if (task.isSuccessful()) {FirebaseUser user = mAuth.getCurrentUser();
             if (user != null) {saveUserToDatabase(user.getUid());
                 showLoadingScreenActivity();} else {
                 showToast("Ошибка при получении текущего пользователя");}} else {
             if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                 showToast("Пользователь с такой почтой уже существует");
             } else {
                 showToast("Неправильная структура эл.почты или нет интернета");
             }}});} else {showToast("Примите условия Пользовательского соглашения");}}



    private void saveUserToDatabase(String userId) {
        clearUserData();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        Map<String, Object> user = new LinkedHashMap<>();
        assert userEmail != null;
        user.put(Constants.KEY_ROLE, userEmail.equals("ferrumslv@gmail.com") ||
                userEmail.equals("danilhafizov123kk@gmail.com") ? "Администратор" : "Пользователь");
        String role = Objects.requireNonNull(user.get(Constants.KEY_ROLE)).toString();
        user.put(Constants.KEY_NAME, Objects.requireNonNull(editTextName.getText()).toString());
        user.put(Constants.KEY_SURNAME, Objects.requireNonNull(editTextSurname.getText()).toString());
        user.put(Constants.KEY_EMAIL, Objects.requireNonNull(editTextEmail.getText()).toString());
        user.put(Constants.KEY_PASSWORD, Objects.requireNonNull(editTextPassword.getText()).toString());
        user.put(Constants.KEY_REGISTRATION_DATE, FieldValue.serverTimestamp());
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, userId);
                    preferenceManager.putString(Constants.KEY_NAME, editTextName.getText().toString());
                    preferenceManager.putString(Constants.KEY_SURNAME,editTextSurname.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL,editTextEmail.getText().toString());
                    preferenceManager.putString(Constants.KEY_ROLE, role);
                }) .addOnFailureListener(exception -> showToast(exception.getMessage()));
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showLoadingScreenActivity()
    {
        Intent intent = new Intent(SignUpActivity.this, LoadingScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showWelcomeActivity()
    {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearUserData() {
        preferenceManager.clearPreferences();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
