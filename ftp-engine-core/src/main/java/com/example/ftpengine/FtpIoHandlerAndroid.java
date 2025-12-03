package com.example.ftpengine;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;

/**
 * Android-compatible IoHandler for the minimal FTP engine.
 * Wraps network events and passes them to the FtpCommandProcessor.
 */
public class FtpIoHandlerAndroid implements IoHandler {

    private final FtpCommandProcessor processor;

    public FtpIoHandlerAndroid(FtpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void sessionCreated(IoSession session) {
        // Initialize per-session FTP context
        session.setAttribute("ftpCtx", new FtpSessionContext());
    }

    @Override
    public void sessionOpened(IoSession session) {
        try {
            // Welcome message to FTP client
            session.write("220 Android FTP Server Ready\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            session.close();
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (!(message instanceof String)) return;

        String line = ((String) message).trim();
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");

        try {
            processor.handle(session, ctx, line);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.write("550 Internal server error\r\n".getBytes());
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        // No special action needed after sending data
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        try {
            session.write("426 Connection closed due to error\r\n".getBytes());
        } catch (IOException ignored) {}
        session.close();
    }

    @Override
    public void sessionClosed(IoSession session) {
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");
        if (ctx != null) ctx.reset();
    }
}