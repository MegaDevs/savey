package com.megadevs.savey.machineclient;

public class User {

    private static User instance;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    private int id;

    private User() {
        this.id = 1;
    }

    public int getId() {
        return id;
    }
}
