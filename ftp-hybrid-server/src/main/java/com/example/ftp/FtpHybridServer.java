package com.example.ftp;

import org.apache.mina.core.service.NioSocketAcceptor;
import java.net.InetSocketAddress;
import com.example.ftp.handler.FtpIoHandlerAndroid;
import com.example.swiftp.AndroidFileSystem;
import com.example.swiftp.SwiftpCommandProcessor;

import java.io.File;

/**
 * Simple hybrid server starter.
 * Replace the NioSocketAcceptor stub (mina-shim) with real MINA in production.
 */
public class FtpHybridServer {
    private NioSocketAcceptor acceptor;
    private final int port;
    private final SwiftpCommandProcessor processor;

    public FtpHybridServer(File rootDir, int port) {
        this.acceptor = new NioSocketAcceptor();
        this.processor = new SwiftpCommandProcessor(new AndroidFileSystem(rootDir));
        this.port = port;
    }

    public void start() throws Exception {
        FtpIoHandlerAndroid handler = new FtpIoHandlerAndroid(processor);
        acceptor.setHandler(handler);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("Hybrid FTP server started on port " + port);
    }

    public void stop() {
        if (acceptor != null) acceptor.dispose();
    }
}
