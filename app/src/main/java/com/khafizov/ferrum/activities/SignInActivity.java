package com.khafizov.ferrum.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    private TextInputEditText editTextLogin, editTextPassword;
    private MaterialButton signInBtn;
    private ImageButton showPasswordBtn;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
        listeners();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        editTextLogin = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        signInBtn = findViewById(R.id.SignInBtn);
        showPasswordBtn = findViewById(R.id.show_password_btn);
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
        signInBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(editTextLogin.getText()).toString();
            String password = Objects.requireNonNull(editTextPassword.getText()).toString();
            loginUser(email, password);});}
    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
          showToast("Пожалуйста заполните все поля!"); return;}
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    showToast("Добро пожаловать!");
                    signIn();} else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        FirebaseAuthInvalidUserException exception =
                        (FirebaseAuthInvalidUserException) task.getException();
                        String errorCode = exception.getErrorCode();
                        if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                          showToast("Пользователь не найден");
                        } else {showToast("Пользователь не найден");}}
                    else {showToast("Пользователь не найден");}}});}

    private void signIn(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
        .whereEqualTo(Constants.KEY_EMAIL, Objects.requireNonNull(editTextLogin.getText()).toString())
        .whereEqualTo(Constants.KEY_PASSWORD, Objects.requireNonNull(editTextPassword.getText()).toString())
        .get()
        .addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null
               && task.getResult().getDocuments().size() > 0) {
           DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
           preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN, true);
           preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
           preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
           preferenceManager.putString(Constants.KEY_ROLE, documentSnapshot.getString(Constants.KEY_ROLE));
           preferenceManager.putString(Constants.KEY_SURNAME, documentSnapshot.getString(Constants.KEY_SURNAME));
           preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
           preferenceManager.putString(Constants.KEY_BIRTHDAY, documentSnapshot.getString(Constants.KEY_BIRTHDAY));
           preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
           preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
           showLoadingScreenActivity();
            } else {
                showToast("Пользователь не найден");}});}

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }



    public void showWelcomeActivity()
    {
        Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void showLoadingScreenActivity()
    {
        Intent intent = new Intent(SignInActivity.this, LoadingScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void reg_btn_Click(View view)
    {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, WelcomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}