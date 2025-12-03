package com.example.ftpengine;

import android.content.Context;

import com.example.ftpengine.filesystem.saf.SAFFileSystem;
// FIXED: Correct package for handler
import com.example.ftpengine.FtpIoHandlerAndroid;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.NioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * FtpEngine:
 * Minimal FTP server core using a patched MINA NioSocketAcceptor.
 * Compatible with SAFFileSystem (Android 11+).
 */
public class FtpEngine {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    /**
     * Constructor.
     *
     * @param context Android context (optional for logging / Toasts)
     * @param fs SAFFileSystem instance representing the FTP root folder
     */
    public FtpEngine(Context context, SAFFileSystem fs) {
        // SAFFileSystem must EXTEND FtpFileSystem or implement IFtpFileSystem
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);
        this.acceptor = new NioSocketAcceptor(handler);
    }

    /**
     * Start the FTP server on the specified port.
     */
    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    /**
     * Stop the FTP server.
     */
    public void stop() {
        if (acceptor != null) {
            acceptor.shutdown();
        }
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}