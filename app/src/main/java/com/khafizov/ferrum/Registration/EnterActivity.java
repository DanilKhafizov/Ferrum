package com.khafizov.ferrum.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.R;

public class EnterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputEditText editTextLogin, editTextPassword;
    private ImageButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        mAuth = FirebaseAuth.getInstance();

        editTextLogin = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.enter_btn);

        loginButton.setOnClickListener(v -> {
            String email = editTextLogin.getText().toString();
            String password = editTextPassword.getText().toString();
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(com.khafizov.ferrum.Registration.EnterActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(com.khafizov.ferrum.Registration.EnterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            FirebaseAuthInvalidUserException exception = (FirebaseAuthInvalidUserException) task.getException();
                            String errorCode = exception.getErrorCode();

                            if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                                Toast.makeText(com.khafizov.ferrum.Registration.EnterActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(com.khafizov.ferrum.Registration.EnterActivity.this, "Пользователь не найден" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(com.khafizov.ferrum.Registration.EnterActivity.this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void reg_btn_Click(View view)
    {
        Intent intent = new Intent(com.khafizov.ferrum.Registration.EnterActivity.this, RegActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }
}