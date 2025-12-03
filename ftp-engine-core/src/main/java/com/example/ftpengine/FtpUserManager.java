package com.example.ftpengine;

import java.util.concurrent.ConcurrentHashMap;

public class FtpUserManager {
    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    public FtpUserManager() {
        users.put("admin", "admin");
    }

    public boolean authenticate(String username, String password) {
        String stored = users.get(username == null ? "" : username);
        return stored != null && stored.equals(password);
    }

    public void addUser(String username, String password) {
        users.put(username, password);
    }
}