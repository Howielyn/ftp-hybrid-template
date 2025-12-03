package com.example.ftp;

import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpSessionContext;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;

import java.nio.charset.StandardCharsets;

/**
 * Android-compatible IoHandler for FTP control connection.
 *
 * Decodes incoming bytes into UTF-8 command lines and passes them to
 * FtpCommandProcessor.
 */
public class FtpIoHandlerAndroid implements IoHandler {

    private final FtpCommandProcessor processor;

    public FtpIoHandlerAndroid(FtpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void sessionCreated(IoSession session) {
        session.setAttribute("ftpCtx", new FtpSessionContext());
        System.out.println("Session created: " + session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) {
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");
        try {
            processor.handle(session, ctx, "220 Welcome to Android FTP Server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionClosed(IoSession session) {
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");
        if (ctx != null) ctx.reset();
        System.out.println("Session closed: " + session.getId());
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (!(message instanceof byte[])) return;

        byte[] bytes = (byte[]) message;
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");

        try {
            String line = new String(bytes, StandardCharsets.UTF_8);
            String[] commands = line.split("\r?\n");

            for (String cmdLine : commands) {
                cmdLine = cmdLine.trim();
                if (!cmdLine.isEmpty()) {
                    processor.handle(session, ctx, cmdLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                processor.handle(session, ctx, "500 Internal server error");
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {}

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        FtpSessionContext ctx = (FtpSessionContext) session.getAttribute("ftpCtx");
        try {
            processor.handle(session, ctx, "500 Internal server error");
        } catch (Exception ignored) {}
    }
}