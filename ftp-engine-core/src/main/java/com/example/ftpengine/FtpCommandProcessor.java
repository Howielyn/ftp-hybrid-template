package com.example.swiftp;

import org.apache.mina.core.session.IoSession;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Very small command processor skeleton inspired by Swiftp.
 * Expand commands (LIST, RETR, STOR, PASV, PORT, etc.) as needed.
 */
public class SwiftpCommandProcessor {
    private final AndroidFileSystem fs;

    public SwiftpCommandProcessor(AndroidFileSystem fs) {
        this.fs = fs;
    }

    public void process(String line, IoSession session) {
        try {
            String cmd = line.trim().split(" ")[0].toUpperCase();
            if (cmd.equals("NOOP")) {
                session.write("200 OK\r\n");
            } else if (cmd.equals("PWD")) {
                session.write("257 \"/\" is current directory.\r\n");
            } else if (cmd.equals("LIST")) {
                String[] items = fs.list(".");
                StringBuilder sb = new StringBuilder();
                for (String it : items) {
                    sb.append(it).append("\r\n");
                }
                session.write("150 Opening ASCII mode data connection for file list.\r\n");
                session.write(sb.toString());
                session.write("226 Transfer complete.\r\n");
            } else if (cmd.equals("STOR")) {
                // Rudimentary: assume second token is filename, no data connection implemented.
                String[] parts = line.split(" ", 2);
                if (parts.length >= 2) {
                    String filename = parts[1].trim();
                    File f = fs.resolve(filename);
                    try (OutputStream os = new FileOutputStream(f)) {
                        os.write(new byte[0]); // placeholder; real data transfer uses data connection
                    }
                    session.write("226 Stored " + filename + "\r\n");
                } else {
                    session.write("501 Syntax error in parameters or arguments.\r\n");
                }
            } else {
                session.write("502 Command not implemented.\r\n");
            }
        } catch (IOException ex) {
            try { session.write("550 Error processing command: " + ex.getMessage() + "\r\n"); } catch (Exception e) {}
        }
    }
}
