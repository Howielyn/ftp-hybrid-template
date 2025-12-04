package com.example.ftpengine;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.AndroidNioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * FTP engine using the clean-room NIO acceptor implementation.
 */
public class FtpEngine {

    private final AndroidNioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(IFtpFileSystem fs) {
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);

        // Your custom acceptor requires IoHandler in constructor
        this.acceptor = new NioSocketAcceptor(handler);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) {
            acceptor.unbind();   // your custom method
            acceptor.dispose();  // your custom method
        }
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}