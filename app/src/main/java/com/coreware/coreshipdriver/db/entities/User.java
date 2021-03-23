package com.coreware.coreshipdriver.db.entities;

import org.jetbrains.annotations.NotNull;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @NotNull
    private Long userId;

    private String address1;
    private String address2;
    private String city;
    private Integer countryId;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String postalCode;
    private String connectedSessionId;
    private String state;
    private String username;

    private boolean isClockedIn = false;

    public String getFullName() {
        StringBuilder fullNameBuilder = new StringBuilder();

        if (firstNameExists()) {
            fullNameBuilder.append(firstName);
        }
        if (firstNameExists() && lastNameExists()) {
            fullNameBuilder.append(" ");
        }
        if (lastNameExists()) {
            fullNameBuilder.append(lastName);
        }

        return fullNameBuilder.toString();
    }

    private boolean firstNameExists() {
        return firstName != null && firstName.length() > 0;
    }

    private boolean lastNameExists() {
        return lastName != null && lastName.length() > 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", countryId=" + countryId +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", connectedSessionId='" + connectedSessionId + '\'' +
                ", state='" + state + '\'' +
                ", username='" + username + '\'' +
                ", isClockedIn=" + isClockedIn +
                '}';
    }

    @NotNull
    public Long getUserId() {
        return userId;
    }
    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public Integer getCountryId() {
        return countryId;
    }
    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getConnectedSessionId() {
        return connectedSessionId;
    }
    public void setConnectedSessionId(String connectedSessionId) {
        this.connectedSessionId = connectedSessionId;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public boolean isClockedIn() {
        return isClockedIn;
    }
    public void setClockedIn(boolean clockedIn) {
        isClockedIn = clockedIn;
    }
}
