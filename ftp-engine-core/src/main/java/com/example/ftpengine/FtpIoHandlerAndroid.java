package com.example.ftpengine;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;

/**
 * Minimal IoHandler implementation to connect TCP input/output
 * to the FTP command processor.
 */
public class FtpIoHandlerAndroid implements IoHandler {

    private final FtpCommandProcessor processor;

    public FtpIoHandlerAndroid(FtpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void sessionCreated(IoSession session) {
        session.setAttribute("ftpCtx", new FtpSessionContext());
    }

    @Override
    public void sessionOpened(IoSession session) {
        session.write("220 Android FTP Server Ready\r\n".getBytes());
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (!(message instanceof String)) return;

        String line = ((String) message).trim();
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");

        processor.handle(session, ctx, line);
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        // no-op
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close();
    }

    @Override
    public void sessionClosed(IoSession session) {
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");
        if (ctx != null) ctx.reset();
    }
}