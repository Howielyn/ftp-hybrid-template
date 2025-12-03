package com.example.ftpengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Minimal local filesystem bridge for the FTP engine.
 * Works on Android (uses java.io only, no NIO).
 */
public class FtpFileSystem {

    private final File root;

    public FtpFileSystem(File rootDir) {
        this.root = rootDir;
        if (!root.exists()) root.mkdirs();
    }

    private File resolve(String path) {
        if (path == null || path.isEmpty()) path = "/";
        if (path.startsWith("/")) path = path.substring(1);
        File f = new File(root, path);
        try {
            // prevent going outside of root folder
            String r = root.getCanonicalPath();
            String c = f.getCanonicalPath();
            if (!c.startsWith(r)) return root;
        } catch (Exception ignored) {}
        return f;
    }

    public boolean exists(String path) {
        return resolve(path).exists();
    }

    public boolean isDirectory(String path) {
        return resolve(path).isDirectory();
    }

    public boolean mkdir(String path) {
        return resolve(path).mkdirs();
    }

    public boolean delete(String path) {
        File f = resolve(path);
        return f.delete();
    }

    public boolean rename(String oldPath, String newPath) {
        File o = resolve(oldPath);
        File n = resolve(newPath);
        return o.renameTo(n);
    }

    public String[] list(String path) {
        File f = resolve(path);
        String[] items = f.list();
        if (items == null) return new String[0];
        Arrays.sort(items);
        return items;
    }

    public byte[] readFile(String path) throws Exception {
        File f = resolve(path);
        FileInputStream in = new FileInputStream(f);
        byte[] data = new byte[(int) f.length()];
        int r = in.read(data);
        in.close();
        if (r < 0) return new byte[0];
        return data;
    }

    public void writeFile(String path, byte[] data) throws Exception {
        File f = resolve(path);
        File parent = f.getParentFile();
        if (!parent.exists()) parent.mkdirs();
        FileOutputStream out = new FileOutputStream(f);
        out.write(data);
        out.flush();
        out.close();
    }
}