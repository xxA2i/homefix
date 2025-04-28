package com.example.homefix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class IndustryFragment extends Fragment {
    private String industry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_industry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null || !getArguments().containsKey("industry")) {
            Toast.makeText(getContext(), "Ошибка: не указана отрасль", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        industry = getArguments().getString("industry");
        if (industry == null || industry.isEmpty()) {
            Toast.makeText(getContext(), "Ошибка: пустая отрасль", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        TextView title = view.findViewById(R.id.industry_title);
        title.setText(industry);

        RecyclerView recyclerView = view.findViewById(R.id.industry_masters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Master> masters = getMastersForIndustry(industry);
        MasterAdapter adapter = new MasterAdapter(masters, requireContext());
        recyclerView.setAdapter(adapter);

        TextView emptyView = view.findViewById(R.id.empty_view);
        if (masters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        // Анимация для заголовка
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        title.startAnimation(fadeIn);
    }

    private List<Master> getMastersForIndustry(String industry) {
        List<Master> filtered = new ArrayList<>();
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            List<Master> allMasters = dbHelper.getAllMasters();
            String lowerCaseIndustry = industry.toLowerCase();

            for (Master master : allMasters) {
                if (master.getSpecialty().toLowerCase().contains(lowerCaseIndustry)) {
                    filtered.add(master);
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
        }
        return filtered;
    }
}