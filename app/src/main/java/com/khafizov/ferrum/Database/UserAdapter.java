package com.khafizov.ferrum.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
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
        // Очистить предыдущее изображение, если оно было загружено ранее
//        Glide.with(holder.itemView.getContext()).clear(holder.userPhoto);

        // Загрузка и отображение изображения пользователя, только если путь к изображению существует
        if (user.getPhoto() != null) {
            Picasso.get().load(user.getPhoto()).into(holder.userPhoto);
        } else {
            // Если у пользователя нет фотографии, отображайте изображение по умолчанию
            holder.userPhoto.setImageResource(R.drawable.default_image);
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
            // Здесь найдите остальные элементы интерфейса карточки пользователя по их идентификаторам
        }
    }
}
