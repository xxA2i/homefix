package com.example.homefix;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private MasterAdapter adapter;
    private List<Master> masters = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyView;
    private EditText searchEditText;
    private DatabaseHelper dbHelper;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Master> allMasters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        if (getContext() == null) return view;
        dbHelper = new DatabaseHelper(getContext());

        searchEditText = view.findViewById(R.id.search_edit_text);
        recyclerView = view.findViewById(R.id.masters_list);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MasterAdapter(masters, getContext());
        recyclerView.setAdapter(adapter);

        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        searchEditText.startAnimation(fadeIn);

        loadMasters();
        setupSearch();
        return view;
    }

    private void loadMasters() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            allMasters.clear();
            allMasters.addAll(dbHelper.getAllMasters());
            masters.clear();
            masters.addAll(allMasters);
            adapter.notifyDataSetChanged();

            progressBar.setVisibility(View.GONE);
            updateEmptyView();
        }, 1000);
    }

    private void filterMasters(String query) {
        List<Master> filtered = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Master master : allMasters) {
            if (master.getName().toLowerCase().contains(lowerCaseQuery) ||
                    master.getSpecialty().toLowerCase().contains(lowerCaseQuery)) {
                filtered.add(master);
            }
        }

        masters.clear();
        masters.addAll(filtered);
        adapter.notifyDataSetChanged();
        updateEmptyView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMasters(s.toString());
            }
        });
    }

    private void updateEmptyView() {
        if (masters.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(searchEditText.getText().toString().isEmpty()
                    ? "Нет доступных мастеров"
                    : "Мастеров не найдено");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}