package com.example.homefix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.MasterViewHolder> {
    private final List<Master> masters;
    private final Context context;

    public MasterAdapter(List<Master> masters, Context context) {
        this.masters = masters;
        this.context = context;
    }

    @NonNull
    @Override
    public MasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_master, parent, false);
        return new MasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterViewHolder holder, int position) {
        Master master = masters.get(position);
        holder.nameTextView.setText(master.getName());
        holder.specialtyTextView.setText(master.getSpecialty());
        holder.ratingTextView.setText(String.format("Рейтинг: %.1f", master.getRating()));
        holder.priceTextView.setText(String.format("от %.0f ₽", master.getMinPrice()));

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        holder.itemView.startAnimation(fadeIn);

        holder.itemView.setOnClickListener(v -> {
            MasterDetailDialog dialog = new MasterDetailDialog(context, master);
            dialog.setOnHireListener(master1 -> {
                Toast.makeText(context, "Мастер " + master1.getName() + " нанят", Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return masters.size();
    }

    static class MasterViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, specialtyTextView, ratingTextView, priceTextView;

        MasterViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.master_name);
            specialtyTextView = itemView.findViewById(R.id.master_specialty);
            ratingTextView = itemView.findViewById(R.id.master_rating);
            priceTextView = itemView.findViewById(R.id.master_price);
        }
    }
}