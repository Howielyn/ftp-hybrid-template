package com.example.ftpengine;

import org.apache.mina.core.service.NioSocketAcceptor;
import java.net.InetSocketAddress;

public class FtpEngine {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(IFtpFileSystem fs) {
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        // Construct acceptor with no arguments
        this.acceptor = new NioSocketAcceptor();

        // Assign handler separately
        this.acceptor.setHandler(new FtpIoHandlerAndroid(processor));

        // Optional: configure session buffer sizes
        this.acceptor.getSessionConfig().setReadBufferSize(1024);
        this.acceptor.getSessionConfig().setReceiveBufferSize(1024);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) {
            acceptor.unbind();
            acceptor.dispose();
        }
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}