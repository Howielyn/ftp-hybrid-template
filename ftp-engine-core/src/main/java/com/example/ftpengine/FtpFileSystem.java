package com.example.ftpengine;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Minimal filesystem interface for the Android FTP engine.
 */
public interface IFtpFileSystem {

    boolean exists(String path);

    boolean mkdir(String path) throws IOException;

    boolean delete(String path) throws IOException;

    boolean rename(String from, String to) throws IOException;

    String[] list(String path) throws IOException;

    byte[] readFile(String path) throws IOException;

    void writeFile(String path, byte[] data) throws IOException;
}