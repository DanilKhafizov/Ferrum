package com.khafizov.ferrum.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.khafizov.ferrum.R;
import org.jetbrains.annotations.Nullable;


public class LanguageFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private Spinner languageSpinner;
    private Button applyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language, container, false);

        languageSpinner = view.findViewById(R.id.spinner_lang);
        applyButton = view.findViewById(R.id.apply_button);

        // Создание адаптера для Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Применение адаптера к Spinner
        languageSpinner.setAdapter(adapter);

        // Обработчик выбора элемента в Spinner
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Действия при выборе языка
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Обработка, если ничего не выбрано
            }
        });

        // Обработчик нажатия на кнопку "Применить"
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedLanguage = (String) languageSpinner.getSelectedItem();
                acceptChangeDialog(selectedLanguage);


            }
        });

        return view;
    }

    // Метод для отображения диалогового окна с выбранным языком
    private void showLanguageDialog(String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы изменили текущий язык приложения на " + language + " язык");
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Действия при нажатии на кнопку "ОК"
            }
        });
        builder.show();
    }

    private void acceptChangeDialog(String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Изменение языка");
        builder.setMessage("Вы правда хотите изменить язык всего приложения на " + language + " ?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLanguageDialog(language);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрыть текущее окно
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
}