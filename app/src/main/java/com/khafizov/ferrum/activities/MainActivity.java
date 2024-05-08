package com.khafizov.ferrum.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.adapters.ImageSliderAdapter;
import com.khafizov.ferrum.models.SharedViewModel;
import com.khafizov.ferrum.models.SharedViewModelApplication;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.PreferenceManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView tvServices, textView;
    private ProgressBar progressBar;
    private SharedViewModel sharedViewModel;
    private HorizontalScrollView horizontalScrollView;
    private CardView cardViewSchedule;
    private ImageButton callClubBtn;
    private BottomNavigationView bottomNavigationView;
    private int currentPage = 0;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listeners();
    }
    private void init(){
        bottomNavigationView = findViewById(R.id.main_menu);
        tvServices = findViewById(R.id.services_tv);
        progressBar = findViewById(R.id.progress_bar);
        textView = findViewById(R.id.textView);
        callClubBtn = findViewById(R.id.call_club_im_btn);
        horizontalScrollView = findViewById(R.id.horizontal_scroll_view);
        cardViewSchedule = findViewById(R.id.card_view_schedule);
        ViewPager viewPager = findViewById(R.id.viewPager);
        int[] images = {R.drawable.pavel, R.drawable.image1, R.drawable.image2,
        R.drawable.image3, R.drawable.image4, R.drawable.image5};
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(adapter);
        startImageSliderAutoScroll(viewPager, images.length);
        sharedViewModel = ((SharedViewModelApplication) getApplication()).
        getViewModelProvider().get(SharedViewModel.class);
    }
    @SuppressLint("NonConstantResourceId")
    private void listeners(){
        cardViewSchedule.setOnClickListener(v -> showScheduleActivity());
        callClubBtn.setOnClickListener(v -> callClub());
        textView.setOnClickListener(v -> callClub());
        if (sharedViewModel.isNewServiceAdded()) {
            getImageUrlsFromFirestore();
            sharedViewModel.setNewServiceAdded(false);}
        else{displayImagesFromSharedPreferences();}
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottom_home:
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
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    finish();
                    return true;}return false;});
    }
    private void startImageSliderAutoScroll(ViewPager viewPager, int imageCount) {
        final Handler handler = new Handler();
        final Runnable update = () -> {
            if (currentPage == imageCount) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 4000);
    }



    private void getImageUrlsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference imagesRef = db.collection(Constants.KEY_COLLECTION_SERVICES);
        imagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> imageUrlList = new ArrayList<>();
                int numDocuments = task.getResult().size();
                if (numDocuments == 0) {
                    showToast("Услуги не найдены");
                    tvServices.setVisibility(View.GONE);
                    horizontalScrollView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }  else {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageUrl = document.getString(Constants.KEY_IMAGE);
                        imageUrlList.add(imageUrl);
                        displayImage(imageUrl);
                    }saveImageUrlsToSharedPreferences(imageUrlList);}}
        });
    }

    private void displayImagesFromSharedPreferences() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Gson gson = new Gson();
        // Получаем список URL-адресов из SharedPreferences
        String json = preferenceManager.getString(Constants.KEY_IMAGE_URL_LIST);
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> imageUrlList = gson.fromJson(json, type);
        if (imageUrlList != null && !imageUrlList.isEmpty()) {
            // Загрузка и отображение изображений по URL-адресам
            for (String imageUrl : imageUrlList) {
                displayImage(imageUrl);
            }
        } else {
            getImageUrlsFromFirestore();
        }
    }

    private void saveImageUrlsToSharedPreferences(List<String> imageUrlList) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        Gson gson = new Gson();
        String json = gson.toJson(imageUrlList);
        preferenceManager.putString(Constants.KEY_IMAGE_URL_LIST, json);
    }

    private void displayImage(String imageUrl) {
        CardView cardView = new CardView(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(getServiceImage(imageUrl));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(525, 525);
        cardLayoutParams.setMargins(30, 0, 30, 0); // отступы между изображениями
        cardView.setLayoutParams(cardLayoutParams);
        // радиус закругления углов для CardView
        cardView.setRadius(20);
        cardView.addView(imageView);
        LinearLayout linearLayout = findViewById(R.id.containerLinear); // Замените на ID вашего LinearLayout в XML макете
        linearLayout.addView(cardView);
        progressBar.setVisibility(View.GONE);
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("QueryPermissionsNeeded")
    private void callClub(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String club_phone = "89876233456";
        intent.setData(Uri.parse("tel:" + club_phone));
        // Проверка наличия приложения для звонков на устройстве
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("Приложения для звонков не найдено");
        }
    }

    private static Bitmap getServiceImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
    public void showScheduleActivity()
    {
        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
        startActivity(intent);
        finish();
    }
    }

