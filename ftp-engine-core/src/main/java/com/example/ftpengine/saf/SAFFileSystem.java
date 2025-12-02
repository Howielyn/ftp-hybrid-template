package com.example.ftpengine;

import java.io.*;

/**
 * Basic filesystem adapter. Replace direct File usage with SAF for Android 11+.
 */
public class FtpFileSystem {
    private final File root;

    public FtpFileSystem(File root) {
        this.root = root;
        if (!root.exists()) root.mkdirs();
    }

    public String[] list(String path) {
        File f = resolveFile(path);
        String[] list = f.list();
        return list != null ? list : new String[0];
    }

    public boolean isDirectory(String path) {
        File f = resolveFile(path);
        return f.exists() && f.isDirectory();
    }

    public boolean exists(String path) {
        File f = resolveFile(path);
        return f.exists();
    }

    public InputStream readFile(String path) throws FileNotFoundException {
        return new FileInputStream(resolveFile(path));
    }

    public OutputStream writeFile(String path) throws FileNotFoundException {
        File f = resolveFile(path);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        return new FileOutputStream(f);
    }

    public boolean delete(String path) {
        return resolveFile(path).delete();
    }

    public boolean mkdir(String path) {
        return resolveFile(path).mkdirs();
    }

    public File resolve(String path) {
        return resolveFile(path);
    }

    private File resolveFile(String path) {
        String p = path == null ? "/" : path;
        if (p.startsWith("/")) p = p.substring(1);
        if (p.isEmpty()) return root;
        return new File(root, p);
    }
}
