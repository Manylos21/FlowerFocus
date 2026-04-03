package com.example.flowerfocus.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "flowers",
    foreignKeys = @ForeignKey(
        entity = Session.class,
        parentColumns = "id",
        childColumns = "sessionId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("sessionId")}
)
public class Flower {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int sessionId;
    public String type;
    public int level;         // 1=petite, 2=moyenne, 3=longue session
    public float progression;
}
