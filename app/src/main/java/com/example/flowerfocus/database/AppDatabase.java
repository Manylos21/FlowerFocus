package com.example.flowerfocus.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.flowerfocus.dao.FlowerDao;
import com.example.flowerfocus.dao.SessionDao;
import com.example.flowerfocus.model.Flower;
import com.example.flowerfocus.model.Session;

@Database(entities = {Session.class, Flower.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SessionDao sessionDao();
    public abstract FlowerDao flowerDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "flowerfocus_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
