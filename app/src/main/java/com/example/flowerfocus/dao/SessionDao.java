package com.example.flowerfocus.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.flowerfocus.model.Session;
import com.example.flowerfocus.model.SessionWithFlower;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    long insertSession(Session session);

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    LiveData<List<Session>> getAllSessions();

    @Transaction
    @Query("SELECT * FROM sessions ORDER BY date DESC")
    LiveData<List<SessionWithFlower>> getAllSessionsWithFlowers();

    @Query("SELECT SUM(realDuration) FROM sessions WHERE status = 'completed'")
    LiveData<Long> getTotalFocusTime();

    @Query("SELECT COUNT(*) FROM sessions WHERE status = 'completed'")
    LiveData<Integer> getCompletedCount();

    @Query("SELECT DISTINCT date(date/1000, 'unixepoch') FROM sessions WHERE status = 'completed' ORDER BY date DESC")
    LiveData<List<String>> getDistinctDays();
}
