package com.example.ftpengine;

import org.apache.mina.core.session.IoSession;

/**
 * Session wrapper (thin). Most state is kept in IoSession attributes for simplicity.
 */
public class FtpSession {
    private final IoSession control;

    public FtpSession(IoSession control) {
        this.control = control;
    }

    public IoSession getControlSession() {
        return control;
    }
}