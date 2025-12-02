package org.apache.mina.core.service;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.mina.core.session.IoSession;

/**
 * Android-friendly simple acceptor that uses ServerSocket and spawns a thread per connection reader.
 * It reads lines (terminated by CRLF or LF) and forwards them to the configured IoHandler.
 *
 * NOTE: This is a simplified acceptor for the template and is NOT a full MINA implementation.
 */
public class NioSocketAcceptor {
    private IoHandler handler;
    private ServerSocket serverSocket;
    private ExecutorService acceptorService;
    private volatile boolean running = false;

    public void setHandler(IoHandler handler) {
        this.handler = handler;
    }

    public void bind(InetSocketAddress addr) throws IOException {
        if (running) return;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(addr);
        running = true;
        acceptorService = Executors.newCachedThreadPool();
        // accept thread
        acceptorService.execute(() -> {
            try {
                while (running && !serverSocket.isClosed()) {
                    final Socket client = serverSocket.accept();
                    acceptorService.execute(() -> handleClient(client));
                }
            } catch (IOException e) {
                if (running) System.err.println("Acceptor error: " + e.getMessage());
            }
        });
        System.out.println("mina-android-patched: bound to " + addr);
    }

    private void handleClient(Socket client) {
        try {
            IoSession session = new IoSession(client);
            // simple reader: read lines and hand to handler
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
            // welcome message (optional)
            session.write("220 FTP Hybrid Server ready\r\n");
            String line;
            while ((line = in.readLine()) != null && running) {
                try {
                    if (handler != null) handler.messageReceived(session, line + "\r\n");
                } catch (Exception ex) {
                    System.err.println("Handler error: " + ex.getMessage());
                }
            }
            try {
                if (handler != null) handler.sessionClosed(session);
            } catch (Exception ex) {}
            session.close();
        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        }
    }

    public void dispose() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (Exception e) {}
        if (acceptorService != null) acceptorService.shutdownNow();
        System.out.println("mina-android-patched: disposed");
    }
}
