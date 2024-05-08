package com.khafizov.ferrum.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.khafizov.ferrum.R;
import com.khafizov.ferrum.models.Trainers;
import java.util.List;

public class TrainersAdapter extends RecyclerView.Adapter<TrainersAdapter.TrainersViewHolder> {
    private final  OnItemClickListener listener;
    private final List<Trainers> trainers;
    public interface OnItemClickListener {
        void onItemClick(int position);}
    public TrainersAdapter(List<Trainers> trainers, OnItemClickListener listener) {
        this.trainers = trainers;
        this.listener = listener;}
    @NonNull
    @Override
    public TrainersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_trainers, parent, false);
        return new TrainersViewHolder(itemView, listener);}
    @Override
    public void onBindViewHolder(@NonNull TrainersViewHolder holder, int position) {
        Trainers trainer = trainers.get(position);
        holder.bind(trainer);}
    @Override
    public int getItemCount() {
        return trainers.size();
    }
    public static class TrainersViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTrainer;
        private final TextView roleTrainer;
        private final ImageView imageTrainer;
        public TrainersViewHolder(View itemView,  OnItemClickListener listener) {
            super(itemView);
            nameTrainer = itemView.findViewById(R.id.trainer_name);
            roleTrainer = itemView.findViewById(R.id.trainer_role);
            imageTrainer = itemView.findViewById(R.id.trainer_image);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);}});}
        public void bind(Trainers trainer) {
            nameTrainer.setText(trainer.getNameTrainer());
            roleTrainer.setText(trainer.getRoleTrainer());
            imageTrainer.setImageBitmap(getTrainersImage(trainer.getImageTrainer()));}}
    private static Bitmap getTrainersImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Установка конфигурации для сохранения качества
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);}}