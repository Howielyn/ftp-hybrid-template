package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

/**
 * Minimal Android-compatible IoHandler interface.
 *
 * This is a clean-room design that mirrors the behavior of the original MINA
 * conceptually but contains no copied logic or internal design.
 *
 * The FTP Engine will use this interface to receive network events.
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
     * Called when a session has been opened (socket connected).
     *
     * @param session The opened session.
     */
    void sessionOpened(IoSession session);

    /**
     * Called when new data has arrived from the client.
     *
     * @param session The session that received the data.
     * @param message The decoded message or raw buffer.
     */
    void messageReceived(IoSession session, Object message);

    /**
     * Called when the handler is expected to send data out through the session.
     *
     * @param session The session to send data to.
     * @param message The message to send.
     */
    void messageSent(IoSession session, Object message);

    /**
     * Called when an exception occurs on the connection.
     *
     * @param session The affected session.
     * @param cause The exception thrown.
     */
    void exceptionCaught(IoSession session, Throwable cause);

    /**
     * Called when the session is being closed.
     *
     * @param session The session being closed.
     */
    void sessionClosed(IoSession session);
}