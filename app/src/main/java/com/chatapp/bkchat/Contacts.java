package com.chatapp.bkchat;

public class Contacts {
    public String username;
    public String description;
    public String email;
    public String image;
    public String coverImage;

    public Contacts() {

    }

    public Contacts(String username, String description, String email, String image, String coverImage) {
        this.username = username;
        this.description = description;
        this.email = email;
        this.image = image;
        this.coverImage = coverImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
