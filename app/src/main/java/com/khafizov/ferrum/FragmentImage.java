package com.khafizov.ferrum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class FragmentImage extends Fragment {
    private ImageView imageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        imageView = view.findViewById(R.id.imageView);

        // Получение изображения из аргументов
        Bundle bundle = getArguments();
        if (bundle != null) {
            int imageResource = bundle.getInt("image");
            imageView.setImageResource(imageResource);
        }

        return view;
    }
}

