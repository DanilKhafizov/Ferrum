package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.adapters.ServicesAdapter;
import com.khafizov.ferrum.models.Service;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity implements ServicesAdapter.OnVkLinkClickListener, ServicesAdapter.OnDeleteServiceClickListener{
    private RecyclerView recyclerView;
    private final List<Service> serviceList = new ArrayList<>();
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private ServicesAdapter servicesAdapter;
    private AppCompatImageButton imAddService;
    private ImageButton backButton;
    private BottomNavigationView bottomNavigationView;
    private String role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        init();
        listeners();
    }

    private void init(){
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        preferenceManager = new PreferenceManager(getApplicationContext());
        imAddService = findViewById(R.id.im_add_service);
        backButton = findViewById(R.id.back_btn);
        bottomNavigationView = findViewById(R.id.main_menu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_services);
        role = preferenceManager.getString(Constants.KEY_ROLE);
    }

    @SuppressLint("NonConstantResourceId")
    private void listeners(){
        loadServices();
        imAddService.setOnClickListener(v -> showAddServiceActivity());
        backButton.setOnClickListener(v -> showMainActivity() );
        if (role.equals("Администратор")){
            imAddService.setVisibility(View.VISIBLE);
        }
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
                    return true;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;
            }
            return false;
        });
    }

    private void loadServices(){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_SERVICES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> fetchedServices = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Service service = new Service();
                        service.setId(document.getId());
                        service.setNameService(document.getString(Constants.KEY_NAME_SERVICE));
                        service.setEmployee1(document.getString(Constants.KEY_NAME));
                        service.setImage(document.getString(Constants.KEY_IMAGE));
                        String fullName = service.getEmployee1().substring(0, service.getEmployee1().indexOf("-") - 1);
                        db.collection(Constants.KEY_COLLECTION_EMPLOYEES)
                                .whereEqualTo(Constants.KEY_NAME, fullName)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                            for (QueryDocumentSnapshot document1 : queryDocumentSnapshots1) {
                                                String vkLink = document1.getString(Constants.KEY_VK_LINK);
                                                service.setVk(vkLink);
                                            }
                        fetchedServices.add(service);
                                    // Проверка на завершение всех запросов
                                    if (fetchedServices.size() == queryDocumentSnapshots.size()) {
                                        serviceList.clear();
                                        serviceList.addAll(fetchedServices);
                                        servicesAdapter = new ServicesAdapter(serviceList, this, this, role);
                                        recyclerView.setAdapter(servicesAdapter);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(e -> showToast("Ошибка при получении данных: " + e.getMessage()));
                    }

                })
                .addOnFailureListener(e -> showToast("Ошибка при получении данных: " + e.getMessage()));
    }


    private void showAddServiceActivity(){
        Intent intent = new Intent(ServicesActivity.this, AddServiceActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMainActivity()
    {
        Intent intent = new Intent(ServicesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void saveImageUrlsToSharedPreferences(List<String> imageUrlList) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Gson gson = new Gson();
        String json = gson.toJson(imageUrlList);
        preferenceManager.putString(Constants.KEY_IMAGE_URL_LIST, json);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ServicesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onVkLinkClick(String vkLink) {
        if (vkLink != null) {
            Uri uri = Uri.parse(vkLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ссылка VK не указана", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDeleteServiceClick(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServicesActivity.this);
        builder.setTitle("Подтверждение удаления")
                .setMessage("Вы уверены что хотите удалить услугу?")
                .setPositiveButton("Да", (dialog, which) -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(Constants.KEY_COLLECTION_SERVICES)
                            .document(service.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                serviceList.remove(service);
                                servicesAdapter.notifyItemRemoved(serviceList.indexOf(service));
                                List<String> updatedImageUrlList = new ArrayList<>();
                                for (Service s : serviceList) {
                                    updatedImageUrlList.add(s.getImage());
                                }
                                saveImageUrlsToSharedPreferences(updatedImageUrlList);
                            })
                            .addOnFailureListener(e -> Toast.makeText(ServicesActivity.this, "Ошибка удаления услуги: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}


