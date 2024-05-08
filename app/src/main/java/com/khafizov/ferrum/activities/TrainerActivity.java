package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

public class TrainerActivity extends AppCompatActivity {
    private TextView trainerName, trainerPhone, trainerRole, trainerVK;
    private ImageView trainerImage;
    private String nameTrainer, phoneTrainer, roleTrainer, vkTrainer, encodedImage, idTrainer, role;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private CardView imageCard;
    private Button deleteBtn;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);
        init();
        listeners();
    }

    private void init(){
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        idTrainer = preferenceManager.getString(Constants.KEY_TRAINER_ID);
        role = preferenceManager.getString(Constants.KEY_ROLE);
        trainerName = findViewById(R.id.trainer_name);
        trainerPhone = findViewById(R.id.trainer_phone);
        trainerRole = findViewById(R.id.trainer_role);
        trainerVK = findViewById(R.id.trainer_vk);
        trainerImage = findViewById(R.id.trainer_image);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.linearLayout);
        imageCard = findViewById(R.id.image_card);
        deleteBtn = findViewById(R.id.delete_btn);
        backBtn = findViewById(R.id.back_btn);
    }
    private void listeners(){
        if (idTrainer != null){
            getTrainerInfo();
            trainerPhone.setOnClickListener(v -> callTrainerPhone());
            trainerVK.setOnClickListener(v -> openVKPage());
            deleteBtn.setOnClickListener(v -> deleteTrainer(idTrainer));
            backBtn.setOnClickListener(v -> showTrainersActivity());
        }
        else{
            showToast("Информация отсутствует");
            finish();
        }

    }

    private void getTrainerInfo() {
        if (idTrainer != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference taskRef = db.collection(Constants.KEY_COLLECTION_EMPLOYEES).document(idTrainer);
            taskRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    nameTrainer = documentSnapshot.getString(Constants.KEY_NAME);
                    phoneTrainer = documentSnapshot.getString(Constants.KEY_PHONE);
                    roleTrainer = documentSnapshot.getString(Constants.KEY_ROLE);
                    vkTrainer = documentSnapshot.getString(Constants.KEY_VK_LINK);
                    encodedImage = documentSnapshot.getString(Constants.KEY_IMAGE);
                    trainerName.setText(nameTrainer);
                    trainerPhone.setText(phoneTrainer);
                    trainerRole.setText(roleTrainer);
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        Bitmap bitmap = getTrainersImage(encodedImage);
                        trainerImage.setImageBitmap(bitmap);
                    }
                }
                if(role.equals("Администратор")){
                    deleteBtn.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                imageCard.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> showToast("Ошибка при получении данных"));
        }
    }

    private void deleteTrainer(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_EMPLOYEES).document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                  showToast("Сотрудник удален");
                  showTrainersActivity();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                     showToast("Ошибка при удалении сотрудника");
                    }
                });
    }

    private void openVKPage(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vkTrainer));
        if (vkTrainer != null && vkTrainer.contains("https://vk.com")) {
            startActivity(intent);
        } else {
            showToast("Ссылка на страницу отсутствует");
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void callTrainerPhone(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneTrainer));
        // Проверка наличия приложения для звонков на устройстве
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("Приложения для звонков не найдено");
        }
    }

    private static Bitmap getTrainersImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Установка конфигурации для сохранения качества
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public void showTrainersActivity()
    {
        Intent intent = new Intent(TrainerActivity.this, TrainersActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}