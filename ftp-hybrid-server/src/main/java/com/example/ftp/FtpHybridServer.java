package com.example.ftphybrid;

import android.content.Context;
import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpUserManager;
import com.example.ftpengine.filesystem.saf.SAFFileSystem;
import org.apache.mina.core.service.NioSocketAcceptor;
import java.net.InetSocketAddress;

/**
 * FTP Engine using NIO selector acceptor + SAF filesystem.
 */
public class FtpEngineHybrid {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngineHybrid(Context context, SAFFileSystem safFs) {
        this.processor = new FtpCommandProcessor(safFs, new FtpUserManager());
        this.acceptor = new NioSocketAcceptor(new FtpIoHandlerAndroid(processor));
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FTP Hybrid Server started on port " + port);
    }

    public void stop() {
        if (acceptor != null) acceptor.shutdown();
        System.out.println("FTP Hybrid Server stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}