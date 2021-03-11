package com.coreware.coreshipdriver.db.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class SessionAndUser {

    @Embedded
    public Session session;

    @Relation(
            parentColumn = "sessionId",
            entityColumn = "connectedSessionId"
    )
    public User user;

    public Session getSession() {
        return session;
    }
    public User getUser() {
        return user;
    }

}
