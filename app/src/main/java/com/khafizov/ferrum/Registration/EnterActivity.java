package com.khafizov.ferrum.Registration;

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
import com.khafizov.ferrum.Activities.MainActivity;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class EnterActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    private TextInputEditText editTextLogin, editTextPassword;
    private MaterialButton signInBtn;
    private ImageButton showPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        preferenceManager = new PreferenceManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();

        editTextLogin = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        signInBtn = findViewById(R.id.SignInBtn);
        showPasswordBtn = findViewById(R.id.show_password_btn);

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

        ImageButton backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(v -> showWelcomeActivity());

        signInBtn.setOnClickListener(v -> {
            String email = editTextLogin.getText().toString();
            String password = editTextPassword.getText().toString();
            loginUser(email, password);
        });

    }

    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(EnterActivity.this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        signIn();
                        showToast("Успешный вход");
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
    private void signIn(){
      //  loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, editTextLogin.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, editTextPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_SURNAME, documentSnapshot.getString(Constants.KEY_SURNAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_BIRTHDAY, documentSnapshot.getString(Constants.KEY_BIRTHDAY));
                        preferenceManager.putString(Constants.KEY_PHONE, documentSnapshot.getString(Constants.KEY_PHONE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        //loading(false);
                        showToast("Пользователь не найден");
                    }
                });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }



//    private void loading(Boolean isLoading){
//        if(isLoading){
//            signInBtn.setVisibility(View.INVISIBLE);
//            //binding.progressBar.setVisibility(View.VISIBLE);
//        }
//        else{
//            signInBtn.setVisibility(View.VISIBLE);
//            // binding.progressBar.setVisibility(View.INVISIBLE);
//        }
//    }

    public void showWelcomeActivity()
    {
        Intent intent = new Intent(EnterActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
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