package com.khafizov.ferrum.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;


import java.util.HashMap;

public class RegActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    TextInputEditText editTextName, editTextSurname, editTextEmail, editTextPassword;
    MaterialButton signUpBtn;
    ProgressBar progressBar;
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
        signUpBtn.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            registerUser(name, surname, email, password);
        });
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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        saveUserToDatabase(user.getUid());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegActivity.this, "Отказано в регистрации. Заполните все поля и проверьте правильность заполнения блоков!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }
    private void saveUserToDatabase(String userId) {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, editTextName.getText().toString());
        user.put(Constants.KEY_SURNAME, editTextSurname.getText().toString());
        user.put(Constants.KEY_EMAIL,editTextEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, editTextPassword.getText().toString());
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


    @Override
    public void onBackPressed() {
        // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
        super.onBackPressed();
    }
}
