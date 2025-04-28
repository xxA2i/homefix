package com.example.homefix;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class MasterDetailDialog extends Dialog {
    private final Master master;
    private OnHireListener hireListener;

    public interface OnHireListener {
        void onHire(Master master);
    }

    public MasterDetailDialog(@NonNull Context context, Master master) {
        super(context);
        this.master = master;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_master_detail);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.7f);
            window.setWindowAnimations(R.style.DialogAnimation); // Добавляем стиль анимации
        }

        TextView nameView = findViewById(R.id.master_detail_name);
        TextView specialtyView = findViewById(R.id.master_detail_specialty);
        RatingBar ratingBar = findViewById(R.id.master_detail_rating_bar);
        TextView ratingView = findViewById(R.id.master_detail_rating);
        TextView priceView = findViewById(R.id.master_detail_price);
        TextView descriptionView = findViewById(R.id.master_detail_description);
        Button hireButton = findViewById(R.id.hire_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        nameView.setText(master.getName());
        specialtyView.setText(master.getSpecialty());
        ratingBar.setRating((float) master.getRating());
        ratingView.setText(String.format("%.1f (%d отзывов)", master.getRating(), master.getReviewCount()));
        priceView.setText(String.format("от %.0f₽", master.getMinPrice()));
        descriptionView.setText(master.getDescription());

        hireButton.setOnClickListener(v -> {
            if (hireListener != null) {
                hireListener.onHire(master);
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }

    public void setOnHireListener(OnHireListener listener) {
        this.hireListener = listener;
    }
}