package com.example.flowerfocus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flowerfocus.model.Flower;
import com.example.flowerfocus.model.Session;
import com.example.flowerfocus.model.SessionWithFlower;
import com.example.flowerfocus.repository.SessionRepository;

import java.util.List;

/**
 * ViewModel gérant l'état des sessions et les interactions avec le repository.
 * Permet de conserver les données lors des changements de configuration (ex: rotation).
 */
public class SessionViewModel extends AndroidViewModel {

    private final SessionRepository repository;

    // LiveData observés par l'écran de Session
    private final MutableLiveData<Long> timerSeconds = new MutableLiveData<>(0L);
    private final MutableLiveData<Float> flowerProgress = new MutableLiveData<>(0f);
    private final MutableLiveData<Boolean> sessionRunning = new MutableLiveData<>(false);

    // Configuration choisie sur l'écran d'accueil
    private long chosenDuration = 0;
    private String chosenCategory = "";

    public SessionViewModel(@NonNull Application application) {
        super(application);
        repository = new SessionRepository(application);
    }

    // ─── Configuration de la session ──────────────────────────────────────────

    public void setConfig(long durationSeconds, String category) {
        chosenDuration = durationSeconds;
        chosenCategory = category;
    }

    public long getChosenDuration() { return chosenDuration; }
    public String getChosenCategory() { return chosenCategory; }

    // ─── État du chronomètre (mis à jour via LiveData) ────────────────────────

    public LiveData<Long> getTimerSeconds() { return timerSeconds; }
    public void setTimerSeconds(long seconds) { timerSeconds.postValue(seconds); }

    public LiveData<Float> getFlowerProgress() { return flowerProgress; }
    public void setFlowerProgress(float progress) { flowerProgress.postValue(progress); }

    public LiveData<Boolean> getSessionRunning() { return sessionRunning; }
    public void setSessionRunning(boolean running) { sessionRunning.postValue(running); }

    // ─── Base de données ──────────────────────────────────────────────────────

    /**
     * Enregistre une session terminée ou abandonnée dans la base de données.
     */
    public void saveCompletedSession(long realDuration, String status) {
        Session session = new Session();
        session.plannedDuration = chosenDuration;
        session.realDuration = realDuration;
        session.date = System.currentTimeMillis();
        session.status = status;
        session.category = chosenCategory;

        Flower flower = new Flower();
        flower.type = pickFlowerType(chosenCategory);
        flower.level = computeLevel(chosenDuration);
        // La progression est de 100% si terminée, sinon calculée au prorata
        flower.progression = status.equals("completed") ? 1.0f : (float) realDuration / chosenDuration;

        repository.insertSessionWithFlower(session, flower);
    }

    private String pickFlowerType(String category) {
        switch (category) {
            case "Travail": return "rose";
            case "Études": return "tulip";
            case "Sport":  return "daisy";
            default:       return "rose";
        }
    }

    /**
     * Détermine le niveau de la fleur selon la durée prévue.
     */
    private int computeLevel(long durationSeconds) {
        if (durationSeconds <= 15 * 60) return 1;
        if (durationSeconds <= 45 * 60) return 2;
        return 3;
    }

    public LiveData<List<SessionWithFlower>> getAllSessionsWithFlowers() {
        return repository.getAllSessionsWithFlowers();
    }

    public LiveData<Long> getTotalFocusTime() {
        return repository.getTotalFocusTime();
    }

    public LiveData<Integer> getCompletedCount() {
        return repository.getCompletedCount();
    }

    public LiveData<List<String>> getDistinctDays() {
        return repository.getDistinctDays();
    }
}
