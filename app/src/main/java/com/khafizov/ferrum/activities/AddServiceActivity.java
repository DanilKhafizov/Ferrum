package com.khafizov.ferrum.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.SharedViewModel;
import com.khafizov.ferrum.models.SharedViewModelApplication;
import com.khafizov.ferrum.utilities.Constants;
import com.khafizov.ferrum.utilities.ImageEncoder;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddServiceActivity extends AppCompatActivity {
    private Spinner employeesSpinner;
    private EditText serviceNameInput, servicePriceInput, edImage;
    private List<String> employeeNames;
    private ArrayAdapter<String> adapter;
    private ImageView imageView;
    private Button addServiceBtn, chooseImage;
    private String image;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        init();
        listeners();
    }

    private void init(){
        imageView = findViewById(R.id.imageView);
        edImage = findViewById(R.id.edit_text);
        chooseImage = findViewById(R.id.choose_image);
        serviceNameInput = findViewById(R.id.service_name_input);
        servicePriceInput = findViewById(R.id.service_price_input);
        addServiceBtn = findViewById(R.id.add_service_button);
        employeesSpinner = findViewById(R.id.employees_spinner);
        employeeNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(AddServiceActivity.this, R.layout.spinner_item, employeeNames);
        adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        sharedViewModel = ((SharedViewModelApplication) getApplication()).getViewModelProvider().get(SharedViewModel.class);
    }

    private void listeners(){
        addServiceBtn.setOnClickListener(v -> saveNewServiceToFirestore());
        chooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        loadEmployees();
    }

    private void loadEmployees() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference employeesRef = db.collection(Constants.KEY_COLLECTION_EMPLOYEES);
        employeesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                employeeNames.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String employeeName = document.getString(Constants.KEY_NAME);
                    if (employeeName != null) {
                        employeeNames.add(employeeName);
                    }
                }
                adapter.notifyDataSetChanged();
                employeesSpinner.setAdapter(adapter);
                employeesSpinner.setVisibility(View.VISIBLE);
            } else {
                showToast("Ошибка в доступе к документу");
            }
        });
    }

    private void saveNewServiceToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> service = new HashMap<>();
        String nameService = serviceNameInput.getText().toString();
        String priceService = servicePriceInput.getText().toString();
        String nameEmployee = employeesSpinner.getSelectedItem() + " - " + priceService + " р.";
        if(!nameService.isEmpty() && !priceService.isEmpty() && !image.isEmpty()) {
            service.put(Constants.KEY_NAME_SERVICE, nameService);
            service.put(Constants.KEY_NAME, nameEmployee);
            service.put(Constants.KEY_IMAGE, image);
            db.collection(Constants.KEY_COLLECTION_SERVICES)
                    .add(service);
            showServiceActivity();
            showToast("Услуга добавлена");
            sharedViewModel.setNewServiceAdded(true);
        }
            else{
                showToast("Заполните все поля");
            }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(mutableBitmap);
                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);
                            paint.setTextSize(65);
                            paint.setStyle(Paint.Style.FILL_AND_STROKE);
                            Rect textBounds = new Rect();
                            paint.getTextBounds(edImage.getText().toString(), 0, edImage.getText().length(), textBounds);
                            int x = (mutableBitmap.getWidth() - textBounds.width()) / 2;
                            int y = mutableBitmap.getHeight() - textBounds.height();
                            canvas.drawText(edImage.getText().toString(), x, y, paint);
                            imageView.setImageBitmap(mutableBitmap);
                            ImageEncoder imageEncoder = new ImageEncoder();
                            image = imageEncoder.encodeImage(mutableBitmap, 300, 200);
                            edImage.setText("");
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public void showServiceActivity()
    {
        Intent intent = new Intent(AddServiceActivity.this, ServicesActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddServiceActivity.this, ServicesActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}