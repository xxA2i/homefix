package com.example.homefix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AddMasterFragment extends Fragment {
    private EditText nameEditText, ratingEditText, priceEditText, descriptionEditText;
    private Button addButton, selectIndustryButton;
    private TextView selectedIndustryText;
    private DatabaseHelper dbHelper;
    private String selectedIndustry = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_master, container, false);
        dbHelper = new DatabaseHelper(getContext());

        nameEditText = view.findViewById(R.id.edit_name);
        ratingEditText = view.findViewById(R.id.edit_rating);
        priceEditText = view.findViewById(R.id.edit_price);
        descriptionEditText = view.findViewById(R.id.edit_description);
        addButton = view.findViewById(R.id.button_add);
        selectIndustryButton = view.findViewById(R.id.btn_select_industry);
        selectedIndustryText = view.findViewById(R.id.tv_selected_industry);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        nameEditText.startAnimation(fadeIn);
        ratingEditText.startAnimation(fadeIn);
        priceEditText.startAnimation(fadeIn);
        descriptionEditText.startAnimation(fadeIn);
        addButton.startAnimation(fadeIn);
        selectIndustryButton.startAnimation(fadeIn);

        selectIndustryButton.setOnClickListener(v -> showIndustrySelectionDialog());
        addButton.setOnClickListener(v -> addMaster());

        return view;
    }

    private void addMaster() {
        String name = nameEditText.getText().toString();
        String ratingStr = ratingEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (name.isEmpty() || ratingStr.isEmpty() || priceStr.isEmpty() || selectedIndustry.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating;
        float price;
        try {
            rating = Float.parseFloat(ratingStr);
            price = Float.parseFloat(priceStr);
            if (rating < 0 || rating > 5) {
                Toast.makeText(getContext(), "Рейтинг должен быть от 0 до 5", Toast.LENGTH_SHORT).show();
                return;
            }
            if (price <= 0) {
                Toast.makeText(getContext(), "Цена должна быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Некорректные числовые значения", Toast.LENGTH_SHORT).show();
            return;
        }

        Master master = new Master(name, selectedIndustry, rating, price, description);
        if (dbHelper.addMaster(master)) {
            Toast.makeText(getContext(), "Мастер добавлен", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Ошибка добавления мастера", Toast.LENGTH_SHORT).show();
        }
    }

    private void showIndustrySelectionDialog() {
        String[] industries = getResources().getStringArray(R.array.repair_industries);
        new AlertDialog.Builder(requireContext())
                .setTitle("Выберите отрасль")
                .setItems(industries, (dialog, which) -> {
                    selectedIndustry = industries[which];
                    selectedIndustryText.setText(selectedIndustry);
                })
                .show();
    }
}