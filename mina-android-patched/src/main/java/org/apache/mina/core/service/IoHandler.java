package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

public interface IoHandler {
    void messageReceived(IoSession session, Object message) throws Exception;
    void sessionClosed(IoSession session) throws Exception;
}
