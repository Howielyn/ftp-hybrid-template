package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

/**
 * Minimal Android-compatible IoHandler interface.
 *
 * This mirrors the conceptual event callbacks of Apache MINA but is written
 * exclusively for your Android FTP Engine.
 *
 * License: Apache License 2.0
 */
public interface IoHandler {

    /**
     * Called when a new socket session is created.
     *
     * @param session The new I/O session.
     */
    void sessionCreated(IoSession session);

    /**
     * Called when a session has been opened (client connected).
     *
     * @param session The opened session.
     */
    void sessionOpened(IoSession session);

    /**
     * Called when data has arrived from the remote client.
     *
     * @param session The session that received the data.
     * @param message The decoded message or raw buffer.
     */
    void messageReceived(IoSession session, Object message);

    /**
     * Called after data has been sent to the remote client.
     *
     * @param session The session to send data to.
     * @param message The message that was sent.
     */
    void messageSent(IoSession session, Object message);

    /**
     * Called when an exception occurs inside the network pipeline.
     *
     * @param session The affected session.
     * @param cause   The thrown exception.
     */
    void exceptionCaught(IoSession session, Throwable cause);

    /**
     * Called when the session is closing or has closed.
     *
     * @param session The session being closed.
     */
    void sessionClosed(IoSession session);
}