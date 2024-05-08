package com.khafizov.ferrum.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khafizov.ferrum.R;

import java.util.Random;


public class ScheduleInfoFragment extends Fragment {
    private LinearLayout trainingContainer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_info, container, false);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        trainingContainer = view.findViewById(R.id.trainingContainer);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String date = bundle.getString("date");
            dateTextView.setText(date);}
        String[] nameServices = {"Персональная тренировка", "Групповая тренировка", "Сайкл-тренировка"};
        String[] trainers = {"Павел Турчанинов", "Альбина Астафьева", "Виталий Загороднов", "Максим Ишмуратов", "Ольга Гайнуллина"};
        String startTime = "08:00";
        int durationMinutes = 0;
        Random random = new Random();
        while (isBeforeEndOfDay(startTime)) {
            String nameService = nameServices[random.nextInt(nameServices.length)];
            String nameTrainer = "";

            switch (nameService) {
                case "Персональная тренировка":
                    durationMinutes = 90;
                    nameTrainer = trainers[random.nextInt(trainers.length)];
                    break;
                case "Групповая тренировка":
                    durationMinutes = 60;
                    nameTrainer = trainers[random.nextInt(trainers.length - 1)];
                    break;
                case "Сайкл-тренировка":
                    durationMinutes = 120;
                    nameTrainer = "Ольга Гайнуллина";
                    break;
            }
            createTrainingContainer(nameService, nameTrainer, startTime, durationMinutes);
            startTime = calculateNextStartTime(startTime, durationMinutes);
        }return view; }

    private boolean isBeforeEndOfDay(String time) {
        // Проверка, что время не позднее, чем 21:00
        int hour = Integer.parseInt(time.substring(0, 2));
        return hour < 21;
    }
    @SuppressLint("DefaultLocale")
    private String calculateNextStartTime(String startTime, int durationMinutes) {
        int startHour = Integer.parseInt(startTime.substring(0, 2));
        int startMinute = Integer.parseInt(startTime.substring(3, 5));
        int nextStartHour = (startHour + (startMinute + durationMinutes) / 60) % 24;
        int nextStartMinute = (startMinute + durationMinutes) % 60;
        return String.format("%02d:%02d", nextStartHour, nextStartMinute);
    }
    private void createTrainingContainer(String nameService, String nameTrainer, String startTime, int durationMinutes) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );
        LinearLayout trainingLayout = new LinearLayout(getContext());
        trainingLayout.setOrientation(LinearLayout.VERTICAL);
        trainingLayout.setBackgroundResource(R.drawable.schedule_bg_container);
        trainingLayout.setLayoutParams(layoutParams);
        TextView nameServiceTextView = new TextView(getContext());
        nameServiceTextView.setTextColor(getResources().getColor(R.color.colorIcon));
        nameServiceTextView.setTypeface(null, Typeface.BOLD);
        nameServiceTextView.setText(nameService);
        TextView nameTrainerTextView = new TextView(getContext());
        nameTrainerTextView.setTextColor(getResources().getColor(R.color.black));
        nameTrainerTextView.setText(nameTrainer);
        switch (nameService) {
            case "Персональная тренировка": {
                int randomIndex = new Random().nextInt(4);
                String[] trainers = {"Павел Турчанинов", "Альбина Астафьева", "Виталий Загороднов", "Максим Ишмуратов"};
                String trainerName = trainers[randomIndex];
                nameTrainerTextView.setText(trainerName);
                break;
            }
            case "Групповая тренировка": {
                int randomIndex = new Random().nextInt(5);
                String[] trainers = {"Павел Турчанинов", "Альбина Астафьева", "Виталий Загороднов", "Максим Ишмуратов", "Ольга Гайнуллина"};
                String trainerName = trainers[randomIndex];
                nameTrainerTextView.setText(trainerName);
                break;
            }
            case "Сайкл-тренировка":
                nameTrainerTextView.setText("Ольга Гайнуллина");
                break;
        }
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int startMinute = Integer.parseInt(startTime.split(":")[1]);
        int endHour = startHour + (durationMinutes / 60);
        int endMinute = startMinute + (durationMinutes % 60);
        if (endMinute >= 60) {
            endHour += 1;
            endMinute -= 60; }
        TextView timeTextView = new TextView(getContext());
        timeTextView.setTextColor(getResources().getColor(R.color.black));
        @SuppressLint("DefaultLocale") String timeRange = startTime + " - " + String.format("%02d", endHour) + ":" + String.format("%02d", endMinute);
        timeTextView.setText(timeRange);
        LinearLayout.LayoutParams nameTextViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameTextViewParams.setMargins(25, 10, 10, 10);
        nameServiceTextView.setLayoutParams(nameTextViewParams);
        nameTrainerTextView.setLayoutParams(nameTextViewParams);
        timeTextView.setLayoutParams(nameTextViewParams);
        trainingLayout.addView(nameServiceTextView);
        trainingLayout.addView(nameTrainerTextView);
        trainingLayout.addView(timeTextView);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );
        containerParams.setMargins(0, 0, 0, 50);
        trainingContainer.addView(trainingLayout, containerParams);}}