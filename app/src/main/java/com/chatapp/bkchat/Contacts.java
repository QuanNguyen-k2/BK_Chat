package com.chatapp.bkchat;

public class Contacts
{
    public String username, description, image;

    public Contacts()
    {

    }

    public Contacts(String username, String description, String image) {
        this.username = username;
        this.description = description;
        this.image = image;
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
}
