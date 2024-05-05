package com.example.natterchatapp.models;

import java.io.Serializable;

public class User implements Serializable {
    private String name,email,image,token;

    public User() {
    }
    public User(String name, String email, String image, String token) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
