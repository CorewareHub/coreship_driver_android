package com.coreware.coreshipdriver.db.entities;

import org.jetbrains.annotations.NotNull;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @NotNull
    private Long userId;

    private String firstName;
    private String lastName;
    private String connectedSessionId;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", connectedSessionId='" + connectedSessionId + '\'' +
                '}';
    }

    @NotNull
    public Long getUserId() {
        return userId;
    }
    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getConnectedSessionId() {
        return connectedSessionId;
    }
    public void setConnectedSessionId(String connectedSessionId) {
        this.connectedSessionId = connectedSessionId;
    }
}
