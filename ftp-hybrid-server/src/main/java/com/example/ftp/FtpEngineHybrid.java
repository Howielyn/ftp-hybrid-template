package com.example.ftp;

import android.content.Context;

import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpUserManager;
import com.example.ftpengine.saf.SAFFileSystem;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.AndroidNioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * FTP Engine using Android-patched NIO acceptor + SAF filesystem.
 *
 * This class manages the FTP server lifecycle and delegates FTP commands
 * to FtpCommandProcessor.
 */
public class FtpEngineHybrid {

    private final AndroidNioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngineHybrid(Context context, SAFFileSystem safFs) {
        // Initialize the FTP command processor with SAF filesystem
        this.processor = new FtpCommandProcessor(safFs, new FtpUserManager());

        // Create the Android-compatible NIO acceptor
        IoHandler handler = new FtpIoHandlerAndroid(processor);
        this.acceptor = new AndroidNioSocketAcceptor(handler);
    }

    /**
     * Start the FTP server on the specified port.
     */
    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FTP Hybrid Server started on port " + port);
    }

    /**
     * Stop the FTP server.
     */
    public void stop() {
        if (acceptor != null) {
            acceptor.shutdown();   // ✅ replaces unbind() + dispose()
        }
        System.out.println("FTP Hybrid Server stopped");
    }

    /**
     * Get the FTP command processor instance.
     */
    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}