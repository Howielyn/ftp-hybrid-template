package com.example.ftpengine.filesystem.saf;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.util.List;

/**
 * SAF-backed FTP FileSystem.
 *
 * Ensures the FTP server operates inside a user-approved SAF directory.
 * On Android 11+ this is the only legal way to write external storage.
 *
 * License: Apache 2.0
 */
public class SAFFileSystem {

    private final Context context;
    private final Uri rootUri;

    public SAFFileSystem(Context context, Uri rootUri) {
        this.context = context.getApplicationContext();
        this.rootUri = rootUri;
    }

    public SAFFileObject getFile(String path) {
        return new SAFFileObject(context, rootUri, path);
    }

    public List<SAFFileObject> listFiles(String path) throws IOException {
        return getFile(path).list();
    }

    public boolean exists(String path) {
        return getFile(path).exists();
    }

    public boolean delete(String path) throws IOException {
        return getFile(path).delete();
    }

    public boolean mkdir(String path) throws IOException {
        return getFile(path).mkdir();
    }

    public boolean rename(String from, String to) throws IOException {
        return getFile(from).renameTo(to);
    }

    public SAFFileObject getRoot() {
        return new SAFFileObject(context, rootUri, "/");
    }
}