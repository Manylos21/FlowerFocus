package com.example.flowerfocus.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowerfocus.R;
import com.example.flowerfocus.ui.profile.ProfileActivity;
import com.example.flowerfocus.ui.session.SessionActivity;
import com.example.flowerfocus.utils.FlowerView;
import com.example.flowerfocus.viewmodel.SessionViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_DURATION = "EXTRA_DURATION";
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";

    private SessionViewModel viewModel;
    private Slider durationSlider;
    private TextView durationLabel;
    private ChipGroup categoryChipGroup;
    private FlowerView previewFlower;

    private final ActivityResultLauncher<String> notifLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (!granted) {
                Toast.makeText(this,
                    "Notifications désactivées. Le timer fonctionnera quand même.",
                    Toast.LENGTH_LONG).show();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this).get(SessionViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        durationSlider    = findViewById(R.id.durationSlider);
        durationLabel     = findViewById(R.id.durationLabel);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        previewFlower     = findViewById(R.id.previewFlower);
        Button startButton   = findViewById(R.id.startButton);
        Button profileButton = findViewById(R.id.profileButton);

        // Show initial value
        durationLabel.setText((int) durationSlider.getValue() + " minutes");

        // Update label and preview as slider moves
        durationSlider.addOnChangeListener((slider, value, fromUser) -> {
            durationLabel.setText((int) value + " minutes");
            // Preview: map duration to progress hint
            float previewProgress = Math.min(value / 120f * 0.6f + 0.4f, 1f);
            previewFlower.setProgress(previewProgress);
        });

        // Update flower type when category changes
        categoryChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                if (chip != null) {
                    previewFlower.setFlowerType(categoryToFlowerType(chip.getText().toString()));
                }
            }
        });

        // Init preview
        previewFlower.setProgress(0.7f);

        requestNotificationPermission();

        startButton.setOnClickListener(v -> launchSession());
        profileButton.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_allowed_apps) {
            startActivity(new Intent(this, AppSelectionActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchSession() {
        int minutes = (int) durationSlider.getValue();
        long durationSeconds = minutes * 60L;

        int checkedId = categoryChipGroup.getCheckedChipId();
        if (checkedId == -1) {
            Toast.makeText(this, "Choisissez une catégorie", Toast.LENGTH_SHORT).show();
            return;
        }
        Chip selectedChip = findViewById(checkedId);
        String category = selectedChip.getText().toString();

        Intent intent = new Intent(this, SessionActivity.class);
        intent.putExtra(EXTRA_DURATION, durationSeconds);
        intent.putExtra(EXTRA_CATEGORY, category);
        startActivity(intent);
    }

    private String categoryToFlowerType(String category) {
        switch (category) {
            case "Études": return "tulip";
            case "Sport":  return "daisy";
            default:       return "rose";
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
