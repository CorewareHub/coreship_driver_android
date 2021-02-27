package com.coreware.coreshipdriver.db.entities;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Session {

    @PrimaryKey
    @NotNull
    private String sessionId;
    private Date dateLoggedIn;

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", dateLoggedIn=" + dateLoggedIn +
                '}';
    }

    @NotNull
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(@NotNull String sessionId) {
        this.sessionId = sessionId;
    }
    public Date getDateLoggedIn() {
        return dateLoggedIn;
    }
    public void setDateLoggedIn(Date dateLoggedIn) {
        this.dateLoggedIn = dateLoggedIn;
    }

}
