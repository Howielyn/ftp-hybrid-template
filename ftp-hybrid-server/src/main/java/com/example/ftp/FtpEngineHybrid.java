package com.example.ftp;

import android.content.Context;
import android.util.Log;

import com.example.ftpengine.FtpCommandProcessor;
import com.example.ftpengine.FtpUserManager;
import com.example.ftpengine.saf.SAFFileSystem;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.AndroidNioSocketAcceptor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * FTP Engine using Android-patched NIO acceptor + SAF filesystem.
 */
public class FtpEngineHybrid {

    private static final String TAG = "FtpEngineHybrid";

    private final AndroidNioSocketAcceptor acceptor;
    private final FtpCommandProcessor processor;

    public FtpEngineHybrid(Context context, SAFFileSystem safFs) {
        this.processor = new FtpCommandProcessor(safFs, new FtpUserManager());

        IoHandler handler = new FtpIoHandlerAndroid(processor);
        this.acceptor = new AndroidNioSocketAcceptor(handler);
    }

    /**
     * Start the FTP server on the specified port.
     */
    public void start(int port) throws Exception {
        // Bind to all interfaces (0.0.0.0) instead of just localhost
        acceptor.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), port));

        // Log all device IPs for clients to connect
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (!addr.isLoopbackAddress() && addr.getHostAddress().contains(".")) {
                    Log.i(TAG, "FTP server reachable at: " + addr.getHostAddress() + ":" + port);
                }
            }
        }

        Log.i(TAG, "FTP Hybrid Server started on port " + port);
    }

    /**
     * Stop the FTP server.
     */
    public void stop() {
        if (acceptor != null) {
            acceptor.shutdown();
            Log.i(TAG, "FTP Hybrid Server stopped");
        }
    }

    /**
     * Get the FTP command processor instance.
     */
    public FtpCommandProcessor getProcessor() {
        return processor;
    }
}