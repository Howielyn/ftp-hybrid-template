package org.apache.mina.core.session;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal IoSession wrapper around java.net.Socket for the template.
 */
public class IoSession {
    private final Socket socket;
    private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<>();

    public IoSession(Socket socket) {
        this.socket = socket;
    }

    public void setAttribute(Object k, Object v) { attributes.put(k, v); }
    public Object getAttribute(Object k) { return attributes.get(k); }

    public void write(Object msg) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.print(msg.toString());
            out.flush();
        } catch (Exception e) {
            System.err.println("IoSession.write error: " + e.getMessage());
        }
    }

    public void close() {
        try { socket.close(); } catch (Exception e) {}
    }

    public Socket getSocket() { return socket; }
}
