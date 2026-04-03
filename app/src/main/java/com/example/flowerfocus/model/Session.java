package com.example.flowerfocus.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class Session {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public long plannedDuration;   // en secondes
    public long realDuration;
    public long date;
    public String status;          // "completed" ou "abandoned"
    public String category;        // "Travail", "Études", "Détente", "Sport"
}
