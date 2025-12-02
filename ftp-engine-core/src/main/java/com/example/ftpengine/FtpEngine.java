package com.example.ftpengine;

import android.content.Context;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.NioSocketAcceptor;
import java.net.InetSocketAddress;

/**
 * FtpEngine (Selector-based NIO acceptor + SAF FileSystem)
 */
public class FtpEngine {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(Context context, SAFFileSystem fs) {
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());
        IoHandler handler = new com.example.ftp.handler.FtpIoHandlerAndroid(processor);
        this.acceptor = new NioSocketAcceptor(handler);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) acceptor.shutdown();
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}