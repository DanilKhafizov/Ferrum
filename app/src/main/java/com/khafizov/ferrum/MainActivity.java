package com.khafizov.ferrum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private int [] images = {R.drawable.pavel, R.drawable.jason, R.drawable.sarah};

    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
    }

        private class ImagePagerAdapter extends FragmentPagerAdapter {
            public ImagePagerAdapter(FragmentManager fm) {
                super(fm);
            }



            @NonNull
            @Override
            public Fragment getItem(int position) {
                FragmentImage fragment = new FragmentImage();
                Bundle bundle = new Bundle();
                int imagesposition = 0;
                bundle.putInt("image", imagesposition);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return images.length;
            }
        }
    }

