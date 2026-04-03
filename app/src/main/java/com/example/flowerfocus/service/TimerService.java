package com.example.flowerfocus.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.flowerfocus.R;
import com.example.flowerfocus.model.Flower;
import com.example.flowerfocus.model.Session;
import com.example.flowerfocus.repository.SessionRepository;
import com.example.flowerfocus.ui.home.AppSelectionActivity;
import com.example.flowerfocus.ui.session.SessionActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service gérant le chronomètre de la session de concentration en arrière-plan.
 * Il surveille également les applications ouvertes pour détecter les infractions.
 */
public class TimerService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP  = "ACTION_STOP";
    public static final String EXTRA_DURATION = "EXTRA_DURATION";
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    public static final String CHANNEL_ID = "timer_channel";

    // Broadcasts pour mettre à jour l'interface utilisateur
    public static final String BROADCAST_TICK = "com.example.flowerfocus.TICK";
    public static final String BROADCAST_ABANDON = "com.example.flowerfocus.ABANDON";
    public static final String EXTRA_ELAPSED = "EXTRA_ELAPSED";
    public static final String EXTRA_TOTAL   = "EXTRA_TOTAL";

    private final IBinder binder = new TimerBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private long totalDuration = 0;
    private long elapsedSeconds = 0;
    private String category = "Général";
    private boolean running = false;
    private Set<String> allowedPackages;
    private Set<String> launcherPackages;
    private SessionRepository repository;

    // Tâche répétée chaque seconde
    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;

            // Vérification de l'application au premier plan
            checkForegroundApp();

            if (!running) return; 

            elapsedSeconds++;
            updateNotification();
            broadcastTick();

            if (elapsedSeconds >= totalDuration) {
                // La session est terminée avec succès (géré par l'activité via le tick)
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Vérifie si l'application actuellement au premier plan est autorisée.
     * Utilise UsageEvents pour une détection précise en temps réel.
     */
    private void checkForegroundApp() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        // Analyse des événements de changement d'app survenus dans les 1.5 dernières secondes
        UsageEvents events = usm.queryEvents(now - 1500, now);
        UsageEvents.Event event = new UsageEvents.Event();
        String lastApp = null;

        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastApp = event.getPackageName();
            }
        }

        if (lastApp != null) {
            // Ignorer si c'est notre propre application
            if (lastApp.equals(getPackageName())) return;
            // Ignorer si l'application est dans la liste blanche de l'utilisateur
            if (allowedPackages.contains(lastApp)) return;
            // Ignorer si c'est le bureau (launcher) ou un composant système
            if (launcherPackages.contains(lastApp) || isSystemUI(lastApp)) return;

            // Application non autorisée détectée !
            Log.d("TimerService", "APP NON AUTORISÉE DÉTECTÉE : " + lastApp);
            abandonSession();
        }
    }

    /**
     * Filtre pour les composants système critiques qui ne doivent pas arrêter la session.
     */
    private boolean isSystemUI(String packageName) {
        return packageName.equals("com.android.systemui")
            || packageName.equals("android")
            || packageName.contains("settings")
            || packageName.contains("inputmethod");
    }

    /**
     * Arrête la session en cours et l'enregistre immédiatement comme abandonnée.
     */
    private void abandonSession() {
        running = false;
        saveSessionToDb("abandoned");

        // Informe l'activité de session pour qu'elle se ferme
        Intent intent = new Intent(BROADCAST_ABANDON);
        sendBroadcast(intent);

        // Affiche un message d'avertissement visuel (Toast)
        handler.post(() -> Toast.makeText(getApplicationContext(), "Session annulée : application non autorisée", Toast.LENGTH_LONG).show());

        showAbandonNotification();
        stopTimer();
    }

    /**
     * Affiche une notification d'annulation pour prévenir l'utilisateur en dehors de l'app.
     */
    private void showAbandonNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("⚠️ Session annulée")
                .setContentText("Vous avez ouvert une application non autorisée.")
                .setSmallIcon(R.drawable.ic_flower_notif)
                .setAutoCancel(true)
                .build();
        if (manager != null) manager.notify(2, notification);
    }

    /**
     * Enregistre les détails de la session (même incomplète) dans la base de données.
     */
    private void saveSessionToDb(String status) {
        Session session = new Session();
        session.plannedDuration = totalDuration;
        session.realDuration = elapsedSeconds;
        session.date = System.currentTimeMillis();
        session.status = status;
        session.category = category;

        Flower flower = new Flower();
        flower.type = categoryToFlowerType(category);
        flower.level = (totalDuration <= 15 * 60) ? 1 : (totalDuration <= 45 * 60 ? 2 : 3);
        flower.progression = (float) elapsedSeconds / totalDuration;

        repository.insertSessionWithFlower(session, flower);
    }

    private String categoryToFlowerType(String category) {
        if (category == null) return "rose";
        switch (category) {
            case "Études": return "tulip";
            case "Sport":  return "daisy";
            default:       return "rose";
        }
    }

    public class TimerBinder extends Binder {
        public TimerService getService() { return TimerService.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        repository = new SessionRepository(getApplication());
        loadLauncherPackages();
    }

    /**
     * Identifie dynamiquement tous les packages de launcher (bureau) pour les autoriser par défaut.
     */
    private void loadLauncherPackages() {
        launcherPackages = new HashSet<>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfos) {
            launcherPackages.add(ri.activityInfo.packageName);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        if (ACTION_START.equals(intent.getAction())) {
            totalDuration = intent.getLongExtra(EXTRA_DURATION, 0);
            category = intent.getStringExtra(EXTRA_CATEGORY);
            elapsedSeconds = 0;
            running = true;
            
            // Chargement des applications autorisées choisies par l'utilisateur
            SharedPreferences prefs = getSharedPreferences(AppSelectionActivity.PREFS_NAME, MODE_PRIVATE);
            allowedPackages = new HashSet<>(prefs.getStringSet(AppSelectionActivity.KEY_ALLOWED_PACKAGES, new HashSet<>()));

            // Passage du service en premier plan (requis pour Android récent)
            startForeground(1, buildNotification(0));
            handler.postDelayed(tickRunnable, 1000);
        } else if (ACTION_STOP.equals(intent.getAction())) {
            stopTimer();
        }

        return START_NOT_STICKY;
    }

    private void stopTimer() {
        running = false;
        handler.removeCallbacks(tickRunnable);
        stopForeground(true);
        stopSelf();
    }

    private void broadcastTick() {
        Intent intent = new Intent(BROADCAST_TICK);
        intent.putExtra(EXTRA_ELAPSED, elapsedSeconds);
        intent.putExtra(EXTRA_TOTAL, totalDuration);
        sendBroadcast(intent);
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, buildNotification(elapsedSeconds));
        }
    }

    private Notification buildNotification(long secondsElapsed) {
        long minutes = secondsElapsed / 60;
        long seconds = secondsElapsed % 60;
        String timeText = String.format("Temps écoulé : %02d:%02d", minutes, seconds);

        Intent activityIntent = new Intent(this, SessionActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🌸 Session en cours")
                .setContentText(timeText)
                .setSmallIcon(R.drawable.ic_flower_notif)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer de concentration",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Affiche le temps de votre session");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    public long getElapsedSeconds() { return elapsedSeconds; }
    public long getTotalDuration()  { return totalDuration; }
    public boolean isRunning()      { return running; }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public void onDestroy() {
        running = false;
        handler.removeCallbacks(tickRunnable);
        super.onDestroy();
    }
}
