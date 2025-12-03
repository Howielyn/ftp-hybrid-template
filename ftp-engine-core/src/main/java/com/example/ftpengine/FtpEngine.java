package com.example.ftpengine;

import android.content.Context;
import com.example.ftpengine.filesystem.saf.SAFFileSystem;
import com.example.ftp.handler.FtpIoHandlerAndroid;

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
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);
        this.acceptor = new NioSocketAcceptor(handler);
    }

    /**
     * Start the FTP server on the specified port.
     *
     * ⚠ Recommended: call in a background thread.
     *
     * @param port TCP port to listen on
     * @throws Exception if binding fails
     */
    public void start(int port) throws Exception {
        // Bind the acceptor (this may block briefly)
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    /**
     * Stop the FTP server and release resources.
     */
    public void stop() {
        if (acceptor != null) {
            acceptor.shutdown();
        }
        System.out.println("FtpEngine stopped");
    }

    /**
     * Access the command processor for custom commands or hooks.
     */
    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}