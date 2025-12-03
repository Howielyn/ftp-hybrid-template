package com.example.ftpengine.filesystem.saf;

import android.content.Context;
import android.net.Uri;
import com.example.ftpengine.FtpFileSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * SAF-backed FTP FileSystem.
 */
public class SAFFileSystem implements FtpFileSystem {

    private final Context context;
    private final Uri rootUri;

    public SAFFileSystem(Context context, Uri rootUri) {
        this.context = context.getApplicationContext();
        this.rootUri = rootUri;
    }

    @Override
    public File resolve(String path) throws IOException {
        return getFile(path).toFile();
    }

    @Override
    public boolean exists(String path) {
        return getFile(path).exists();
    }

    @Override
    public boolean delete(String path) throws IOException {
        return getFile(path).delete();
    }

    @Override
    public boolean mkdir(String path) throws IOException {
        return getFile(path).mkdir();
    }

    @Override
    public boolean rename(String from, String to) throws IOException {
        return getFile(from).renameTo(to);
    }

    @Override
    public List<File> list(String path) throws IOException {
        return getFile(path).listFiles();
    }

    @Override
    public File getRoot() throws IOException {
        return getFile("/");
    }

    private SAFFileObject getFile(String path) {
        return new SAFFileObject(context, rootUri, path);
    }
}