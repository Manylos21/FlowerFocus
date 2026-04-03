package com.example.flowerfocus.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.flowerfocus.dao.FlowerDao;
import com.example.flowerfocus.dao.SessionDao;
import com.example.flowerfocus.database.AppDatabase;
import com.example.flowerfocus.model.Flower;
import com.example.flowerfocus.model.Session;
import com.example.flowerfocus.model.SessionWithFlower;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionRepository {

    private final SessionDao sessionDao;
    private final FlowerDao flowerDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SessionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        sessionDao = db.sessionDao();
        flowerDao = db.flowerDao();
    }

    // Insertion session + flower
    public void insertSessionWithFlower(Session session, Flower flower) {
        executor.execute(() -> {
            long sessionId = sessionDao.insertSession(session);
            flower.sessionId = (int) sessionId;
            flowerDao.insertFlower(flower);
        });
    }

    public LiveData<List<SessionWithFlower>> getAllSessionsWithFlowers() {
        return sessionDao.getAllSessionsWithFlowers();
    }

    public LiveData<Long> getTotalFocusTime() {
        return sessionDao.getTotalFocusTime();
    }

    public LiveData<Integer> getCompletedCount() {
        return sessionDao.getCompletedCount();
    }

    public LiveData<List<String>> getDistinctDays() {
        return sessionDao.getDistinctDays();
    }
}
