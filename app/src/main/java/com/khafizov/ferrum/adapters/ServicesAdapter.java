package com.khafizov.ferrum.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.Service;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder> {

    public interface OnVkLinkClickListener {
        void onVkLinkClick(String vkLink);
    }

    public interface OnDeleteServiceClickListener {
        void onDeleteServiceClick(Service service);
    }
    private final List<Service> services;
    private final OnVkLinkClickListener vkLinkClickListener;
    private final OnDeleteServiceClickListener deleteServiceClickListener;
    private final String userRole;
    public ServicesAdapter(List<Service> services, OnVkLinkClickListener vkLinkClickListener, OnDeleteServiceClickListener deleteServiceClickListener, String userRole) {
        this.services = services;
        this.vkLinkClickListener = vkLinkClickListener;
        this.deleteServiceClickListener = deleteServiceClickListener;
        this.userRole = userRole;
    }
    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_services, parent, false);
        return new ServicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        Service service = services.get(position);
        holder.vkButton.setOnClickListener(view -> {
            if (vkLinkClickListener != null && service.getVk().contains("https://vk.com")) {
                    vkLinkClickListener.onVkLinkClick(service.getVk());
            }
            else{
                Toast.makeText(holder.itemView.getContext(), "Ссылка на ВК отсутствует", Toast.LENGTH_SHORT).show();
            }
        });
        if (userRole.equals("Администратор")) {
            holder.deleteService.setVisibility(View.VISIBLE);
            holder.deleteService.setOnClickListener(v -> {
                if (deleteServiceClickListener != null) {
                    deleteServiceClickListener.onDeleteServiceClick(services.get(position));
                }
            });
        } else {
            holder.deleteService.setVisibility(View.GONE);
        }
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServicesViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameService;
        private final TextView nameEmployee1;
        private final Button vkButton;
        private final ImageButton deleteService;

        public ServicesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameService = itemView.findViewById(R.id.nameService);
            nameEmployee1 = itemView.findViewById(R.id.nameEmployee1);
            vkButton = itemView.findViewById(R.id.vk_button);
            deleteService = itemView.findViewById(R.id.delete_service);
        }

        public void bind(Service service) {
            imageView.setImageBitmap(getServiceImage(service.getImage()));
            nameService.setText(service.getNameService());
            nameEmployee1.setText(service.getEmployee1());
        }
    }



    private static Bitmap getServiceImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Установка конфигурации для сохранения качества

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


}