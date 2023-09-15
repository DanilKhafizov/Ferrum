package com.khafizov.ferrum.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khafizov.ferrum.Activities.TrainersActivity;
import com.khafizov.ferrum.R;


public class PavelFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // здесь можно выполнить нужные действия, например, закрыть текущее окно или перейти на другой экран
                Intent intent = new Intent(getActivity(), TrainersActivity.class);
                startActivity(intent);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_pavel, container, false);
    }
}