package com.example.ftpengine;

import java.io.IOException;
import java.net.*;

/**
 * Simple data connection helper supporting passive and active mode.
 */
public class FtpDataConnection {
    private ServerSocket pasvSocket;
    private Socket activeSocket;
    private Socket acceptedSocket;

    // Start passive on an ephemeral port (port 0) or fixed port
    public void startPassive(int port) throws IOException {
        pasvSocket = new ServerSocket(port);
    }

    // For passive, accept returns the client connection
    public Socket accept() throws IOException {
        if (pasvSocket == null) throw new IllegalStateException("Passive socket not started");
        acceptedSocket = pasvSocket.accept();
        return acceptedSocket;
    }

    // Connect to client (active mode)
    public void connectActive(String host, int port) throws IOException {
        activeSocket = new Socket();
        activeSocket.connect(new InetSocketAddress(host, port), 10000);
    }

    public boolean isConnected() {
        return (activeSocket != null && activeSocket.isConnected()) ||
               (acceptedSocket != null && acceptedSocket.isConnected());
    }

    public Socket getSocket() {
        if (activeSocket != null) return activeSocket;
        return acceptedSocket;
    }

    public int getLocalPort() {
        if (pasvSocket != null) return pasvSocket.getLocalPort();
        return -1;
    }

    public void stop() {
        try { if (acceptedSocket != null) acceptedSocket.close(); } catch (Exception ignored) {}
        try { if (activeSocket != null) activeSocket.close(); } catch (Exception ignored) {}
        try { if (pasvSocket != null) pasvSocket.close(); } catch (Exception ignored) {}
    }
}