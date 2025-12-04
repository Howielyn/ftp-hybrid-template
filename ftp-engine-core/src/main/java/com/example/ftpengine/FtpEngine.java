package com.example.ftpengine;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.AndroidNioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * FTP engine using the Android-compatible NIO acceptor.
 */
public class FtpEngine {

    private final AndroidNioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(IFtpFileSystem fs) {
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);

        // Instantiate the AndroidNioSocketAcceptor with IoHandler
        this.acceptor = new AndroidNioSocketAcceptor(handler);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) {
            acceptor.shutdown();   // ✅ stop server
        }
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}