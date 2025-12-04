package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Minimal Android-compatible clean-room NIO socket acceptor.
 *
 * This class completely replaces Apache MINA's NioSocketAcceptor for Android.
 * It manages:
 *   - TCP listening
 *   - Non-blocking accept
 *   - Non-blocking read
 *   - IoSession lifecycle
 *   - IoHandler callback events
 *
 * License: Apache License 2.0
 */
public class NioSocketAcceptor implements Runnable {

    private final IoHandler handler;

    private final AtomicLong sessionIdGen = new AtomicLong(0);

    private volatile boolean running = false;

    private Selector selector;
    private ServerSocketChannel serverChannel;

    private Thread loopThread;

    public NioSocketAcceptor(IoHandler handler) {
        this.handler = handler;
    }

    // ------------------------------------------------------------
    // BIND + START LOOP
    // ------------------------------------------------------------

    /**
     * Begin listening for incoming TCP connections.
     */
    public synchronized void bind(InetSocketAddress address) throws IOException {
        if (running) return;

        selector = Selector.open();

        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(address);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        running = true;

        loopThread = new Thread(this, "Android-NioSocketAcceptor");
        loopThread.start();

        System.out.println("[Acceptor] Listening on " + address);
    }

    // ------------------------------------------------------------
    // STOP SERVER
    // ------------------------------------------------------------

    /**
     * Unbind the listening socket (MINA-style).
     */
    public synchronized void unbind() {
        running = false;
        if (selector != null) selector.wakeup();
    }

    /**
     * Fully shutdown and close all resources (MINA-style).
     */
    public synchronized void dispose() {
        unbind();
    }

    // ------------------------------------------------------------
    // MAIN LOOP
    // ------------------------------------------------------------

    @Override
    public void run() {
        try {
            mainLoop();
        } catch (Exception e) {
            System.err.println("[Acceptor] Loop crashed: " + e);
        } finally {
            cleanup();
        }
    }

    private void mainLoop() throws IOException {
        while (running) {
            selector.select(200);

            Set<SelectionKey> selected = selector.selectedKeys();
            Iterator<SelectionKey> it = selected.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    // ------------------------------------------------------------
    // ACCEPT
    // ------------------------------------------------------------

    private void handleAccept() {
        try {
            SocketChannel client = serverChannel.accept();
            if (client == null) return;

            client.configureBlocking(false);

            IoSession session = new IoSession(
                    sessionIdGen.incrementAndGet(),
                    client
            );

            SelectionKey key = client.register(selector, SelectionKey.OP_READ);
            key.attach(session);

            handler.sessionCreated(session);
            handler.sessionOpened(session);

        } catch (Exception e) {
            System.err.println("[Acceptor] Failed to accept client: " + e);
        }
    }

    // ------------------------------------------------------------
    // READ
    // ------------------------------------------------------------

    private void handleRead(SelectionKey key) {
        IoSession session = (IoSession) key.attachment();
        SocketChannel channel = session.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(8192);

        try {
            int read = channel.read(buf);

            if (read == -1) {
                closeSession(session);
                return;
            }

            buf.flip();
            byte[] data = new byte[buf.remaining()];
            buf.get(data);

            handler.messageReceived(session, data);

        } catch (IOException e) {
            handler.exceptionCaught(session, e);
            closeSession(session);
        }
    }

    // ------------------------------------------------------------
    // CLOSE SESSION
    // ------------------------------------------------------------

    private void closeSession(IoSession session) {
        try {
            handler.sessionClosed(session);
        } catch (Exception ignore) {}

        session.close();
    }

    // ------------------------------------------------------------
    // FINAL CLEANUP
    // ------------------------------------------------------------

    private void cleanup() {
        try {
            if (serverChannel != null) serverChannel.close();
        } catch (Exception ignore) {}

        try {
            if (selector != null) selector.close();
        } catch (Exception ignore) {}

        running = false;
        System.out.println("[Acceptor] Shutdown complete");
    }
}