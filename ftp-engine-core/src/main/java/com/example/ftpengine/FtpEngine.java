package com.example.ftpengine;

import org.apache.mina.core.service.NioSocketAcceptor;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * FtpEngine: starts and stops the FTP service using the patched MINA acceptor.
 *
 * Usage:
 *   FtpEngine engine = new FtpEngine(new File("/sdcard/ftproot"), 2121);
 *   engine.start(); // starts listener
 *   engine.stop();
 *
 * Note: ensure your acceptor implementation (mina-android-patched) invokes the
 * FtpCommandProcessor when it receives command lines.
 */
public class FtpEngine {
    private final File rootDir;
    private final int port;
    private NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(File rootDir, int port) {
        this.rootDir = rootDir;
        this.port = port;
        this.processor = new FtpCommandProcessor(new FtpFileSystem(rootDir), new FtpUserManager());
    }

    /**
     * Start listening. This uses the mina-android-patched NioSocketAcceptor which
     * expects an IoHandler that delegates to the command processor.
     */
    public void start() throws Exception {
        acceptor = new NioSocketAcceptor();
        // The IoHandler implementation (in ftp-hybrid-server) should call:
        //    processor.handleCommand(session, line)
        // We just expose the processor getter so that glue code can use it.
        acceptor.setHandler(new com.example.ftp.handler.FtpIoHandlerAndroid(processor));
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) acceptor.dispose();
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}