package com.example.ftpengine;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.core.service.IoHandler;
import java.net.InetSocketAddress;

public class FtpEngine {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngine(IFtpFileSystem fs) {
        this.processor = new FtpCommandProcessor(fs, new FtpUserManager());

        // MINA 1.x: NioSocketAcceptor constructor accepts IoHandler
        IoHandler handler = new FtpIoHandlerAndroid(processor);
        this.acceptor = new NioSocketAcceptor(handler);

        // Optional: set read buffer size
        this.acceptor.getDefaultConfig().setReadBufferSize(1024);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FtpEngine started on port " + port);
    }

    public void stop() {
        if (acceptor != null) {
            acceptor.unbindAll(); // MINA 1.x method
            acceptor.dispose();
        }
        System.out.println("FtpEngine stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}