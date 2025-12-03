package com.example.ftpengine;

import java.io.IOException;

public interface IFtpFileSystem {
    boolean exists(String path);
    boolean delete(String path) throws IOException;
    boolean mkdir(String path) throws IOException;
    boolean rename(String from, String to) throws IOException;
    String[] list(String path) throws IOException;
    byte[] readFile(String path) throws IOException;
    void writeFile(String path, byte[] data) throws IOException;
}