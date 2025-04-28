package com.example.homefix;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button startSearchButton = view.findViewById(R.id.start_search_button);
        startSearchButton.setOnClickListener(v -> showIndustrySelectionDialog());

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        startSearchButton.startAnimation(fadeIn);

        return view;
    }

    private void showIndustrySelectionDialog() {
        try {
            String[] industries = getResources().getStringArray(R.array.repair_industries);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Выберите отрасль ремонта")
                    .setItems(industries, (dialog, which) -> {
                        try {
                            IndustryFragment fragment = new IndustryFragment();
                            Bundle args = new Bundle();
                            args.putString("industry", industries[which]);
                            fragment.setArguments(args);

                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка загрузки отраслей", Toast.LENGTH_SHORT).show();
        }
    }
}