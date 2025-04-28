package com.example.homefix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TopUpFragment extends Fragment {
    private EditText amountEditText;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        prefs = requireActivity().getSharedPreferences("HomeFixPrefs", Context.MODE_PRIVATE);

        amountEditText = view.findViewById(R.id.amount_edit_text);
        Button btn100 = view.findViewById(R.id.btn_100);
        Button btn500 = view.findViewById(R.id.btn_500);
        Button btn1000 = view.findViewById(R.id.btn_1000);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        amountEditText.startAnimation(fadeIn);
        btn100.startAnimation(fadeIn);
        btn500.startAnimation(fadeIn);
        btn1000.startAnimation(fadeIn);
        confirmButton.startAnimation(fadeIn);

        btn100.setOnClickListener(v -> amountEditText.setText("100"));
        btn500.setOnClickListener(v -> amountEditText.setText("500"));
        btn1000.setOnClickListener(v -> amountEditText.setText("1000"));

        confirmButton.setOnClickListener(v -> {
            String amountStr = amountEditText.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    float amount = Float.parseFloat(amountStr);
                    if (amount <= 0) throw new NumberFormatException();

                    String username = prefs.getString("username", "");
                    float currentBalance = prefs.getFloat("balance_" + username, 0f); // Уникальный ключ
                    prefs.edit()
                            .putFloat("balance_" + username, currentBalance + amount)
                            .apply();

                    Toast.makeText(getContext(), "Баланс пополнен на " + amount + " ₽", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}