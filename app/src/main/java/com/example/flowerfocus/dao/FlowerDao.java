package com.example.flowerfocus.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.flowerfocus.model.Flower;

/**
 * Interface d'accès aux données (DAO) pour les entités Flower.
 */
@Dao
public interface FlowerDao {

    @Insert
    long insertFlower(Flower flower);

    /**
     * Récupère la fleur associée à une session spécifique.
     */
    @Query("SELECT * FROM flowers WHERE sessionId = :sessionId LIMIT 1")
    Flower getFlowerBySessionId(int sessionId);

    /**
     * Compte le nombre total de fleurs enregistrées.
     */
    @Query("SELECT COUNT(*) FROM flowers")
    int getTotalFlowers();
}
