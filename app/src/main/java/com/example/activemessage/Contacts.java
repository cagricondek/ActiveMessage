package com.example.activemessage;

public class Contacts {

        public Contacts()
        {

        }

    public Contacts(String username, String status, String profileImage) {
        this.username = username;
        this.status = status;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String username,status,profileImage;
}
