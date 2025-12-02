package com.example.ftp.handler;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import com.example.swiftp.SwiftpCommandProcessor;

/**
 * IoHandler that receives raw command lines and forwards to SwiftpCommandProcessor.
 * This is the glue between MINA networking and Swiftp command engine.
 */
public class FtpIoHandlerAndroid implements IoHandler {
    private final SwiftpCommandProcessor processor;

    public FtpIoHandlerAndroid(SwiftpCommandProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message instanceof String) {
            String line = (String) message;
            processor.process(line, session);
        } else {
            session.write("500 Only ASCII commands supported\r\n");
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // cleanup if needed
    }
}
