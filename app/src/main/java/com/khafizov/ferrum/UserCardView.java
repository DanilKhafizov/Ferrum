package com.khafizov.ferrum;

import static android.view.View.inflate;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khafizov.ferrum.Database.User;
import com.khafizov.ferrum.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

public class UserCardView extends LinearLayout {

    private ImageView mImageView;
    private TextView mNameTextView, mSurnameTextView, mRoleTextView, mBirthdateTextView,
            mPhoneNumberTextView, mEmailTextView;

    public UserCardView(Context context) {
        super(context);
        init();
    }

    public UserCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_list_layout, this);
        mImageView = findViewById(R.id.user_image);
        mNameTextView = findViewById(R.id.user_name);
        mSurnameTextView = findViewById(R.id.user_surname);
        mRoleTextView = findViewById(R.id.user_role);
        mBirthdateTextView = findViewById(R.id.user_birthday);
        mPhoneNumberTextView = findViewById(R.id.user_phone);
        mEmailTextView = findViewById(R.id.user_email);
    }

    void setUserInfo(User user) {
        if (user != null && user.getPhotoUrl() != null) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.error_user_image)
                    .into(mImageView);
        }
        user.setRole("Пользователь");
        mNameTextView.setText(user.getName());
        mSurnameTextView.setText(user.getSurname());
        mRoleTextView.setText(user.getRole());
        mBirthdateTextView.setText(user.getBirthday());
        mPhoneNumberTextView.setText(user.getPhone());
        mEmailTextView.setText(user.getEmail());
    }
}