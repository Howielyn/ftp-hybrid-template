package com.example.ftpengine.saf;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.util.List;

/**
 * SAF-backed FTP FileSystem.
 */
public class SAFFileSystem implements IFtpFileSystem {

    private final Context context;
    private final Uri rootUri;

    public SAFFileSystem(Context context, Uri rootUri) {
        this.context = context.getApplicationContext();
        this.rootUri = rootUri;
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
        return getFile(from).renameTo(getFile(to));
    }

    @Override
    public List<String> list(String path) throws IOException {
        return getFile(path).list();
    }

    @Override
    public byte[] readFile(String path) throws IOException {
        return getFile(path).read();
    }

    @Override
    public void writeFile(String path, byte[] data) throws IOException {
        getFile(path).write(data);
    }

    private SAFFileObject getFile(String path) {
        return new SAFFileObject(context, rootUri, path);
    }
}