package com.example.ftp;

import android.content.Context;

import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpUserManager;
import com.example.ftpengine.saf.SAFFileSystem;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.NioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * FTP Engine using custom Android-safe NIO acceptor + SAF filesystem.
 */
public class FtpEngineHybrid {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngineHybrid(Context context, SAFFileSystem safFs) {
        // Build processor with SAF-backed filesystem
        this.processor = new FtpCommandProcessor(safFs, new FtpUserManager());

        // Create handler
        IoHandler handler = new FtpIoHandlerAndroid(processor);

        // ❗ Your custom acceptor REQUIRES a handler in its constructor
        this.acceptor = new NioSocketAcceptor(handler);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FTP Hybrid Server started on port " + port);
    }

    public void stop() {
        try {
            acceptor.unbind();
        } catch (Exception ignored) {}

        try {
            acceptor.shutdown();
        } catch (Exception ignored) {}

        System.out.println("FTP Hybrid Server stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}