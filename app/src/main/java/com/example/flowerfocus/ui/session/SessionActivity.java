package com.example.flowerfocus.ui.session;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowerfocus.R;
import com.example.flowerfocus.service.TimerService;
import com.example.flowerfocus.ui.home.HomeActivity;
import com.example.flowerfocus.utils.FlowerView;
import com.example.flowerfocus.viewmodel.SessionViewModel;

/**
 * Activité gérant l'écran de la session de concentration en cours.
 */
public class SessionActivity extends AppCompatActivity {

    private SessionViewModel viewModel;
    private FlowerView flowerView;
    private TextView timerText;
    private TextView categoryText;
    private Button abandonButton;

    private TimerService timerService;
    private boolean serviceBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TimerService.TimerBinder tb = (TimerService.TimerBinder) binder;
            timerService = tb.getService();
            serviceBound = true;
            
            // Si le service a déjà arrêté le chrono (infraction), on ferme tout de suite
            if (!timerService.isRunning()) {
                finish();
            } else {
                syncWithService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private final BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TimerService.BROADCAST_TICK.equals(intent.getAction())) {
                long elapsed = intent.getLongExtra(TimerService.EXTRA_ELAPSED, 0);
                long total   = intent.getLongExtra(TimerService.EXTRA_TOTAL, 1);

                timerText.setText(String.format("%02d:%02d", elapsed / 60, elapsed % 60));
                float progress = (float) elapsed / total;
                flowerView.setProgress(progress);
                viewModel.setFlowerProgress(progress);
                viewModel.setTimerSeconds(elapsed);

                if (elapsed >= total) {
                    onSessionCompleted(total);
                }
            } else if (TimerService.BROADCAST_ABANDON.equals(intent.getAction())) {
                // Signal d'abandon reçu du service
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        viewModel     = new ViewModelProvider(this).get(SessionViewModel.class);
        flowerView    = findViewById(R.id.flowerView);
        timerText     = findViewById(R.id.timerText);
        categoryText  = findViewById(R.id.categoryText);
        abandonButton = findViewById(R.id.abandonButton);

        if (getIntent() != null && getIntent().hasExtra(HomeActivity.EXTRA_DURATION)) {
            long duration = getIntent().getLongExtra(HomeActivity.EXTRA_DURATION, 0);
            String category = getIntent().getStringExtra(HomeActivity.EXTRA_CATEGORY);
            viewModel.setConfig(duration, category);
        }

        categoryText.setText(viewModel.getChosenCategory());
        flowerView.setFlowerType(categoryToFlowerType(viewModel.getChosenCategory()));

        abandonButton.setOnClickListener(v -> confirmAbandon());

        // On enregistre le récepteur ici pour qu'il soit actif même en arrière-plan
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.BROADCAST_TICK);
        filter.addAction(TimerService.BROADCAST_ABANDON);
        registerReceiver(tickReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        if (savedInstanceState == null) {
            if (!hasUsageStatsPermission()) {
                requestUsageStatsPermission();
            } else {
                startTimerService();
            }
        }

        Intent bindIntent = new Intent(this, TimerService.class);
        bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Vérification au retour dans l'appli
        if (serviceBound && timerService != null) {
            if (!timerService.isRunning()) {
                finish();
            } else {
                syncWithService();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }
        try {
            unregisterReceiver(tickReceiver);
        } catch (Exception ignored) {}
        super.onDestroy();
    }

    private void syncWithService() {
        if (!serviceBound || timerService == null) return;
        long elapsed = timerService.getElapsedSeconds();
        long total   = timerService.getTotalDuration();
        if (total > 0) {
            timerText.setText(String.format("%02d:%02d", elapsed / 60, elapsed % 60));
            flowerView.setProgress((float) elapsed / total);
        }
    }

    private void startTimerService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_START);
        intent.putExtra(TimerService.EXTRA_DURATION, viewModel.getChosenDuration());
        intent.putExtra(TimerService.EXTRA_CATEGORY, viewModel.getChosenCategory());
        startForegroundService(intent);
    }

    private void stopTimerService() {
        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP);
        startService(intent);
        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }
    }

    private void confirmAbandon() {
        new AlertDialog.Builder(this)
            .setTitle("Abandonner la session ?")
            .setMessage("La fleur sera enregistrée mais incomplète.")
            .setPositiveButton("Abandonner", (d, w) -> {
                long elapsed = serviceBound && timerService != null ? timerService.getElapsedSeconds() : 0;
                viewModel.saveCompletedSession(elapsed, "abandoned");
                stopTimerService();
                finish();
            })
            .setNegativeButton("Continuer", null)
            .show();
    }

    private void onSessionCompleted(long total) {
        stopTimerService();
        viewModel.saveCompletedSession(total, "completed");
        new AlertDialog.Builder(this)
            .setTitle("🌸 Session terminée !")
            .setMessage("Bravo ! Votre fleur a été récoltée.")
            .setPositiveButton("Voir mon profil", (d, w) -> finish())
            .setCancelable(false)
            .show();
    }

    private String categoryToFlowerType(String category) {
        if (category == null) return "rose";
        switch (category) {
            case "Études": return "tulip";
            case "Sport":  return "daisy";
            default:       return "rose";
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        try {
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) { return false; }
    }

    private void requestUsageStatsPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permission requise")
                .setMessage("Pour détecter si vous changez d'application, FlowerFocus a besoin de l'accès aux statistiques d'utilisation.")
                .setPositiveButton("Paramètres", (d, w) -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)))
                .setNegativeButton("Annuler", (d, w) -> finish())
                .setCancelable(false).show();
    }

    @Override
    public void onBackPressed() { confirmAbandon(); }
}
