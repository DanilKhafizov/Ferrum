package com.khafizov.ferrum.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;

    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());
        holder.userSurname.setText(user.getSurname());
        holder.userBirthday.setText(user.getBirthday());
        holder.userPhone.setText(user.getPhone());
        holder.userRole.setText(user.getRole());
        holder.userEmail.setText(user.getEmail());
        if (user.getPhoto() != null){
            holder.userPhoto.setImageBitmap(getUsersImage(user.getPhoto()));
        }
        else{
            int defaultImageResourceId = R.drawable.default_image; // Replace with the actual resource ID of the default image
            Drawable defaultImage = ContextCompat.getDrawable(context, defaultImageResourceId);
            holder.userPhoto.setImageDrawable(defaultImage);
        }


    }
    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        } else {
            return 0;
        }
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView userSurname;
        public TextView userBirthday;
        public TextView userPhone;
        public TextView userRole;
        public TextView userEmail;
        public ImageView userPhoto;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userSurname = itemView.findViewById(R.id.user_surname);
            userBirthday = itemView.findViewById(R.id.user_birthday);
            userPhone = itemView.findViewById(R.id.user_phone);
            userRole = itemView.findViewById(R.id.user_role);
            userEmail = itemView.findViewById(R.id.user_email);
            userPhoto = itemView.findViewById(R.id.user_image);
        }
    }
    private static Bitmap getUsersImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Установка конфигурации для сохранения качества
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
}
