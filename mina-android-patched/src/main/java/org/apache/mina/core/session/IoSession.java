package org.apache.mina.core.session;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal clean-room IoSession for Android.
 *
 * Represents a single TCP client connection and provides basic
 * read/write operations using Java NIO.
 *
 * License: Apache License 2.0
 */
public class IoSession {

    private final long id;
    private final SocketChannel channel;
    private final ConcurrentHashMap<String, Object> attributes =
            new ConcurrentHashMap<>();

    private volatile boolean open = true;

    public IoSession(long id, SocketChannel channel) {
        this.id = id;
        this.channel = channel;
    }

    public long getId() {
        return id;
    }

    public SocketAddress getRemoteAddress() {
        return channel.socket().getRemoteSocketAddress();
    }

    public SocketAddress getLocalAddress() {
        return channel.socket().getLocalSocketAddress();
    }

    public boolean isOpen() {
        return open && channel.isOpen();
    }

    public void close() {
        open = false;
        try {
            channel.close();
        } catch (IOException e) {
            // ignore; closed anyway
        }
    }

    /**
     * Write raw bytes to the client.
     *
     * @param data bytes to write.
     * @return number of bytes written.
     */
    public int write(byte[] data) throws IOException {
        if (!isOpen()) return -1;
        ByteBuffer buf = ByteBuffer.wrap(data);
        return channel.write(buf);
    }

    /**
     * Read up to len bytes into buffer.
     */
    public int read(byte[] buffer) throws IOException {
        if (!isOpen()) return -1;
        ByteBuffer buf = ByteBuffer.wrap(buffer);
        return channel.read(buf);
    }

    /**
     * Session attribute helpers.
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public SocketChannel getChannel() {
        return channel;
    }
}