package com.example.flowerfocus.model;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Classe de liaison (POJO) pour représenter une session et sa fleur associée.
 * Utilisée par Room pour effectuer des jointures automatiques entre les tables sessions et flowers.
 */
public class SessionWithFlower {
    @Embedded
    public Session session;

    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    public Flower flower;
}
