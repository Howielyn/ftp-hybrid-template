package com.example.ftpengine.saf;

import android.content.Context;
import android.net.Uri;
import com.example.ftpengine.IFtpFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SAF-backed FTP FileSystem for Android 11+.
 */
public class SAFFileSystem implements IFtpFileSystem {

    private final Context context;
    private final Uri rootUri;

    public SAFFileSystem(Context context, Uri rootUri) {
        this.context = context.getApplicationContext();
        this.rootUri = rootUri;
    }

    private SAFFileObject getFile(String path) {
        return new SAFFileObject(context, rootUri, path);
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
        List<SAFFileObject> files = getFile(path).list();
        List<String> names = new ArrayList<>();
        for (SAFFileObject f : files) names.add(f.getPath().substring(f.getPath().lastIndexOf('/') + 1));
        return names.toArray(new String[0]);
    }

    @Override
    public byte[] readFile(String path) throws IOException {
        try (InputStream in = getFile(path).openInput();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int r;
            while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
            return out.toByteArray();
        }
    }

    @Override
    public void writeFile(String path, byte[] data) throws IOException {
        try (OutputStream out = getFile(path).openOutput(false)) {
            out.write(data);
            out.flush();
        }
    }
}