package com.example.homefix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ProfileFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private boolean isLoggedIn = false;
    private SharedPreferences prefs;
    private LinearLayout loginLayout;
    private LinearLayout profileLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dbHelper = new DatabaseHelper(getContext());
        prefs = requireActivity().getSharedPreferences("HomeFixPrefs", Context.MODE_PRIVATE);

        loginLayout = view.findViewById(R.id.login_layout);
        profileLayout = view.findViewById(R.id.profile_layout);

        checkLoginStatus();

        if (isLoggedIn) {
            showProfile(view);
        } else {
            showLoginForm(view);
        }

        return view;
    }

    private void showLoginForm(View view) {
        loginLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);

        Button loginButton = view.findViewById(R.id.login_button);
        Button registerButton = view.findViewById(R.id.register_button);
        EditText username = view.findViewById(R.id.username);
        EditText password = view.findViewById(R.id.password);

        // Clear fields when showing the form
        username.setText("");
        password.setText("");

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        loginButton.startAnimation(fadeIn);
        registerButton.startAnimation(fadeIn);
        username.startAnimation(fadeIn);
        password.startAnimation(fadeIn);

        loginButton.setOnClickListener(v -> {
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();

            if (dbHelper.checkUser(usernameText, passwordText)) {
                loginUser(usernameText, view);
            } else {
                Toast.makeText(getContext(), "Неверные данные", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> {
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();

            if (validateInput(usernameText, passwordText)) {
                String result = dbHelper.addUser(usernameText, passwordText);
                if (result.equals("SUCCESS")) {
                    // Reset balance for new user
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("balance_" + usernameText, 0f);
                    editor.apply();
                    loginUser(usernameText, view);
                } else if (result.equals("USERNAME_EXISTS")) {
                    Toast.makeText(getContext(), "Имя пользователя уже занято", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showProfile(View view) {
        loginLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);

        String username = prefs.getString("username", "");
        TextView welcomeText = view.findViewById(R.id.welcome_text);
        welcomeText.setText("Добро пожаловать, " + username + "!");

        TextView balanceText = view.findViewById(R.id.balance_text);
        float balance = prefs.getFloat("balance_" + username, 0f); // Уникальный ключ для пользователя
        balanceText.setText(String.format("Баланс: %.2f ₽", balance));

        Button logoutButton = view.findViewById(R.id.logout_button); // Исправлено с findById
        Button topUpButton = view.findViewById(R.id.top_up_button);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        welcomeText.startAnimation(fadeIn);
        balanceText.startAnimation(fadeIn);
        logoutButton.startAnimation(fadeIn);
        topUpButton.startAnimation(fadeIn);

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            showLoginForm(view);
        });

        topUpButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, new TopUpFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void loginUser(String username, View view) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.apply();
        showProfile(view);
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(getContext(), "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkLoginStatus() {
        isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        // Ensure username is still valid
        if (isLoggedIn && prefs.getString("username", "").isEmpty()) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            isLoggedIn = false;
        }
    }
}