package com.example.ftpengine;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal in-memory user manager. Extend to use persistent storage.
 */
public class FtpUserManager {
    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    public FtpUserManager() {
        // default test account
        users.put("admin", "admin");
    }

    public boolean authenticate(String username, String password) {
        if (username == null) username = "";
        String stored = users.get(username);
        return stored != null && stored.equals(password);
    }

    public void addUser(String username, String password) {
        users.put(username, password);
    }

    public String getHomeDir(String username) {
        // return root by default; override for user homes
        return "/";
    }
}