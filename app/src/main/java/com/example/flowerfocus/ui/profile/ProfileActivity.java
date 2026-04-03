package com.example.flowerfocus.ui.profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowerfocus.R;
import com.example.flowerfocus.viewmodel.SessionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private SessionViewModel viewModel;
    private FlowerHistoryAdapter adapter;
    private TextView totalTimeText;
    private TextView completedCountText;
    private TextView streakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(SessionViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mon Profil");
        }

        totalTimeText      = findViewById(R.id.totalTimeText);
        completedCountText = findViewById(R.id.completedCountText);
        streakText         = findViewById(R.id.streakText);

        RecyclerView recyclerView = findViewById(R.id.flowerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlowerHistoryAdapter();
        recyclerView.setAdapter(adapter);

        observeData();
    }

    private void observeData() {
        viewModel.getAllSessionsWithFlowers().observe(this, sessions ->
                adapter.submitList(sessions));

        viewModel.getTotalFocusTime().observe(this, totalSeconds -> {
            if (totalSeconds == null) totalSeconds = 0L;
            long hours   = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            totalTimeText.setText(String.format("%dh %02dmin", hours, minutes));
        });

        viewModel.getCompletedCount().observe(this, count -> {
            if (count == null) count = 0;
            totalTimeText.setText(count + " fleur" + (count > 1 ? "s" : ""));
            completedCountText.setText(count + " fleur" + (count > 1 ? "s" : ""));
        });

        viewModel.getDistinctDays().observe(this, days -> {
            int streak = computeStreak(days);
            streakText.setText(streak + " jour" + (streak > 1 ? "s" : ""));
        });
    }

    /**
     * Computes the consecutive day streak from a DESC-sorted list of date strings (yyyy-MM-dd).
     * Uses Calendar for API 24 compatibility (no java.time.LocalDate).
     */
    private int computeStreak(List<String> days) {
        if (days == null || days.isEmpty()) return 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        int streak = 0;
        for (String dayStr : days) {
            try {
                Calendar day = Calendar.getInstance();
                day.setTime(sdf.parse(dayStr));
                day.set(Calendar.HOUR_OF_DAY, 0);
                day.set(Calendar.MINUTE, 0);
                day.set(Calendar.SECOND, 0);
                day.set(Calendar.MILLISECOND, 0);

                Calendar expected = (Calendar) today.clone();
                expected.add(Calendar.DAY_OF_YEAR, -streak);

                if (day.equals(expected)) {
                    streak++;
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }
        return streak;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
