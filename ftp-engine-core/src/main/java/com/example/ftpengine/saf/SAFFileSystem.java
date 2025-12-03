package com.example.ftpengine.saf;

import android.content.Context;
import android.net.Uri;
import com.example.ftpengine.IFtpFileSystem;

import java.io.IOException;

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
    public boolean mkdir(String path) throws IOException {
        return getFile(path).mkdir();
    }

    @Override
    public boolean delete(String path) throws IOException {
        return getFile(path).delete();
    }

    @Override
    public boolean rename(String from, String to) throws IOException {
        return getFile(from).renameTo(to);
    }

    @Override
    public String[] list(String path) throws IOException {
        return getFile(path).listNames();
    }

    @Override
    public byte[] readFile(String path) throws IOException {
        return getFile(path).readBytes();
    }

    @Override
    public void writeFile(String path, byte[] data) throws IOException {
        getFile(path).writeBytes(data);
    }

    private SAFFileObject getFile(String path) {
        return new SAFFileObject(context, rootUri, path);
    }
}