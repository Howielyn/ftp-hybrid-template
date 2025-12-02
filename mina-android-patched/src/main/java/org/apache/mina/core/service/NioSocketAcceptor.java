package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Minimal Android-compatible NIO socket acceptor.
 *
 * This class listens on a TCP port, accepts clients, and fires IoHandler
 * callbacks for data events. It is intentionally simple but fully functional.
 *
 * License: Apache License 2.0
 */
public class NioSocketAcceptor implements Runnable {

    private final IoHandler handler;
    private final AtomicLong sessionIdGen = new AtomicLong();
    private volatile boolean running = false;

    private Selector selector;
    private ServerSocketChannel server;

    private Thread loopThread;

    public NioSocketAcceptor(IoHandler handler) {
        this.handler = handler;
    }

    /**
     * Bind the acceptor to a TCP port.
     */
    public void bind(InetSocketAddress address) throws IOException {
        selector = Selector.open();

        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.socket().bind(address);

        server.register(selector, SelectionKey.OP_ACCEPT);

        running = true;
        loopThread = new Thread(this, "NioSocketAcceptor-Loop");
        loopThread.start();
    }

    /**
     * Stop the acceptor and close everything.
     */
    public void shutdown() {
        running = false;
        if (selector != null) {
            selector.wakeup();
        }
    }

    @Override
    public void run() {
        try {
            mainLoop();
        } catch (Exception e) {
            // server loop died
        } finally {
            cleanup();
        }
    }

    private void mainLoop() throws IOException {
        while (running) {
            selector.select(200);

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    acceptConnection();
                } else if (key.isReadable()) {
                    readFromClient(key);
                }
            }
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel client = server.accept();
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
    }

    private void readFromClient(SelectionKey key) {
        IoSession session = (IoSession) key.attachment();
        SocketChannel client = session.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(8192);

        try {
            int read = client.read(buf);

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

    private void closeSession(IoSession session) {
        try {
            handler.sessionClosed(session);
        } catch (Exception ignore) {}

        session.close();
    }

    private void cleanup() {
        try {
            if (server != null) server.close();
        } catch (Exception ignore) {}

        try {
            if (selector != null) selector.close();
        } catch (Exception ignore) {}
    }
}