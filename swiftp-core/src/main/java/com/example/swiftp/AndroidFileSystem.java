package com.example.swiftp;

import java.io.File;
import java.io.IOException;

/**
 * Simplified Android file system adapter.
 * Replace File usages with SAF (Storage Access Framework) calls in production.
 */
public class AndroidFileSystem {
    private final File root;

    public AndroidFileSystem(File root) {
        this.root = root;
    }

    public boolean isDirectory(String path) {
        File f = new File(root, path);
        return f.isDirectory();
    }

    public String[] list(String path) {
        File f = new File(root, path);
        String[] list = f.list();
        return list == null ? new String[0] : list;
    }

    public long getSize(String path) {
        File f = new File(root, path);
        return f.exists() ? f.length() : 0;
    }

    public boolean delete(String path) {
        File f = new File(root, path);
        return f.delete();
    }

    public void ensureDir(String path) {
        File f = new File(root, path);
        if (!f.exists()) f.mkdirs();
    }

    public File resolve(String path) {
        return new File(root, path);
    }
}
