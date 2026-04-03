package com.example.flowerfocus.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowerfocus.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppSelectionActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "AllowedAppsPrefs";
    public static final String KEY_ALLOWED_PACKAGES = "allowed_packages";

    private SharedPreferences prefs;
    private Set<String> allowedPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Applications autorisées");
        }

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        allowedPackages = new HashSet<>(prefs.getStringSet(KEY_ALLOWED_PACKAGES, new HashSet<>()));

        RecyclerView recyclerView = findViewById(R.id.appRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<AppInfo> apps = getInstalledApps();
            runOnUiThread(() -> {
                recyclerView.setAdapter(new AppAdapter(apps));
            });
        }).start();
    }

    private List<AppInfo> getInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppInfo> appList = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {
            // Filter out system apps and self
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 || 
                packageInfo.packageName.contains("google.android.apps.maps") ||
                packageInfo.packageName.contains("android.music")) {
                
                if (packageInfo.packageName.equals(getPackageName())) continue;

                appList.add(new AppInfo(
                    packageInfo.loadLabel(pm).toString(),
                    packageInfo.packageName,
                    packageInfo.loadIcon(pm)
                ));
            }
        }
        Collections.sort(appList, (a, b) -> a.name.compareToIgnoreCase(b.name));
        return appList;
    }

    private static class AppInfo {
        String name;
        String packageName;
        android.graphics.drawable.Drawable icon;

        AppInfo(String name, String packageName, android.graphics.drawable.Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
        private final List<AppInfo> apps;

        AppAdapter(List<AppInfo> apps) { this.apps = apps; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_selection, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AppInfo app = apps.get(position);
            holder.appName.setText(app.name);
            holder.appIcon.setImageDrawable(app.icon);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(allowedPackages.contains(app.packageName));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    allowedPackages.add(app.packageName);
                } else {
                    allowedPackages.remove(app.packageName);
                }
                prefs.edit().putStringSet(KEY_ALLOWED_PACKAGES, allowedPackages).apply();
            });
        }

        @Override
        public int getItemCount() { return apps.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView appIcon;
            TextView appName;
            CheckBox checkBox;

            ViewHolder(View itemView) {
                super(itemView);
                appIcon = itemView.findViewById(R.id.appIcon);
                appName = itemView.findViewById(R.id.appName);
                checkBox = itemView.findViewById(R.id.appCheckBox);
                itemView.setOnClickListener(v -> checkBox.performClick());
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
