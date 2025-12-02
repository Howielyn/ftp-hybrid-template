package com.example.ftphybrid;

import com.example.ftpengine.FtpCommandProcessor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import java.nio.charset.StandardCharsets;

/**
 * Android-compatible IoHandler for FTP control connection.
 *
 * This decodes incoming bytes, splits into command lines, and
 * passes them to FtpCommandProcessor.
 */
public class FtpIoHandlerAndroid implements IoHandler {

    private final FtpCommandProcessor processor;

    public FtpIoHandlerAndroid(FtpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void sessionCreated(IoSession session) {}

    @Override
    public void sessionOpened(IoSession session) {}

    @Override
    public void sessionClosed(IoSession session) {}

    @Override
    public void messageReceived(IoSession session, byte[] message) {
        try {
            String line = new String(message, StandardCharsets.UTF_8);
            String[] commands = line.split("\r?\n");
            for (String cmdLine : commands) {
                processor.handleCommand(cmdLine.trim(), session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {}

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
    }
}
