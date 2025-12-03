package com.example.ftp;

import com.example.ftpengine.FtpCommandProcessor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;

import java.nio.charset.StandardCharsets;

/**
 * Android-compatible IoHandler for FTP control connection.
 *
 * Decodes incoming bytes into UTF-8 command lines and passes them to
 * FtpCommandProcessor. Handles exceptions gracefully.
 */
public class FtpIoHandlerAndroid implements IoHandler {

    private final FtpCommandProcessor processor;

    public FtpIoHandlerAndroid(FtpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void sessionCreated(IoSession session) {
        // Optional: log new session
        System.out.println("Session created: " + session.getId());
    }

    @Override
    public void sessionOpened(IoSession session) {
        // Optional: send welcome message
        processor.sendReply(session, "220 Welcome to Android FTP Server");
    }

    @Override
    public void sessionClosed(IoSession session) {
        // Optional: log session close
        System.out.println("Session closed: " + session.getId());
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (!(message instanceof byte[])) return;

        byte[] bytes = (byte[]) message;
        try {
            String line = new String(bytes, StandardCharsets.UTF_8);
            String[] commands = line.split("\r?\n");
            for (String cmdLine : commands) {
                cmdLine = cmdLine.trim();
                if (!cmdLine.isEmpty()) {
                    processor.handleCommand(cmdLine, session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        // Optional: log sent message
        // System.out.println("Sent: " + message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        try {
            processor.sendReply(session, "500 Internal server error");
        } catch (Exception ignore) {}
    }
}