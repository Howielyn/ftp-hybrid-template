package com.example.ftpengine;

import java.net.Socket;

/**
 * Holds FTP session state for each connected client.
 * This is a minimal clean-room design (Android compatible).
 */
public class FtpSessionContext {

    public boolean loggedIn = false;
    public String username = null;
    public String cwd = "/";
    public String transferType = "I"; // I = binary, A = ASCII

    // Active mode data socket
    public String dataHost = null;
    public int dataPort = -1;
    public Socket activeDataSocket = null;

    // Passive mode data server
    public int pasvPort = -1;
    public Socket passiveDataSocket = null;

    public void reset() {
        loggedIn = false;
        username = null;
        cwd = "/";
        transferType = "I";
        dataHost = null;
        dataPort = -1;
        if (activeDataSocket != null) {
            try { activeDataSocket.close(); } catch (Exception ignored) {}
            activeDataSocket = null;
        }
        if (passiveDataSocket != null) {
            try { passiveDataSocket.close(); } catch (Exception ignored) {}
            passiveDataSocket = null;
        }
        pasvPort = -1;
    }
}