package com.example.ftp;

import android.content.Context;

import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpUserManager;
import com.example.ftpengine.saf.SAFFileSystem;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.NioSocketAcceptor;

import java.net.InetSocketAddress;

public class FtpEngineHybrid {

    private final NioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngineHybrid(Context context, SAFFileSystem safFs) {
        this.processor = new FtpCommandProcessor(safFs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);

        // Use custom acceptor constructor
        this.acceptor = new NioSocketAcceptor(handler);
    }

    public void start(int port) throws Exception {
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("FTP Hybrid Server started on port " + port);
    }

    public void stop() {
        if (acceptor != null) {
            // Custom acceptor uses shutdown()
            acceptor.shutdown();
        }
        System.out.println("FTP Hybrid Server stopped");
    }

    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}