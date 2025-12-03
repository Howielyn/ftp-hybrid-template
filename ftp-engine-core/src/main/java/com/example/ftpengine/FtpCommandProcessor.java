package com.example.ftpengine;

import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

/**
 * Clean-room minimal FTP command processor for Android.
 */
public class FtpCommandProcessor {

    private final FtpFileSystem fs;
    private final FtpUserManager users;

    public FtpCommandProcessor(FtpFileSystem fs, FtpUserManager users) {
        this.fs = fs;
        this.users = users;
    }

    private void reply(IoSession session, String msg) {
        session.write((msg + "\r\n").getBytes());
    }

    public void handle(IoSession session, FtpSessionContext ctx, String line) {
        if (line == null) return;
        String[] parts = line.split(" ", 2);

        String cmd = parts[0].toUpperCase(Locale.ROOT);
        String arg = (parts.length > 1 ? parts[1] : null);

        switch (cmd) {
            case "USER":
                ctx.username = arg;
                reply(session, "331 User name okay, need password");
                break;

            case "PASS":
                if (users.authenticate(ctx.username, arg)) {
                    ctx.loggedIn = true;
                    reply(session, "230 User logged in, proceed");
                } else {
                    reply(session, "530 Not logged in");
                }
                break;

            case "PWD":
                reply(session, "257 \"" + ctx.cwd + "\" is current directory");
                break;

            case "CWD":
                if (arg == null) {
                    reply(session, "501 Missing directory");
                } else {
                    File tryDir = new File(fs.resolve(ctx.cwd + "/" + arg).getPath());
                    if (tryDir.exists() && tryDir.isDirectory()) {
                        ctx.cwd = normalize("/" + arg);
                        reply(session, "250 Directory changed");
                    } else {
                        reply(session, "550 Failed to change directory");
                    }
                }
                break;

            case "TYPE":
                ctx.transferType = arg;
                reply(session, "200 Type set");
                break;

            case "PORT":
                handlePort(session, ctx, arg);
                break;

            case "PASV":
                handlePasv(session, ctx);
                break;

            case "LIST":
                handleList(session, ctx);
                break;

            case "RETR":
                handleRetr(session, ctx, arg);
                break;

            case "STOR":
                handleStor(session, ctx, arg);
                break;

            case "QUIT":
                reply(session, "221 Goodbye");
                session.close();
                break;

            default:
                reply(session, "502 Command not implemented");
        }
    }

    private String normalize(String p) {
        if (p == null || p.isEmpty()) return "/";
        if (!p.startsWith("/")) p = "/" + p;
        return p.replace("//", "/");
    }

    private void handlePort(IoSession session, FtpSessionContext ctx, String arg) {
        try {
            String[] nums = arg.split(",");
            ctx.dataHost = nums[0] + "." + nums[1] + "." + nums[2] + "." + nums[3];
            ctx.dataPort = Integer.parseInt(nums[4]) * 256 + Integer.parseInt(nums[5]);
            reply(session, "200 PORT command successful");
        } catch (Exception e) {
            reply(session, "501 Syntax error");
        }
    }

    private void handlePasv(IoSession session, FtpSessionContext ctx) {
        try {
            ServerSocket ss = new ServerSocket(0); // random port
            ctx.pasvPort = ss.getLocalPort();
            ctx.passiveDataSocket = ss.accept();

            int p1 = ctx.pasvPort / 256;
            int p2 = ctx.pasvPort % 256;

            String response = "227 Entering Passive Mode (127,0,0,1," + p1 + "," + p2 + ")";
            reply(session, response);
        } catch (Exception e) {
            reply(session, "425 Can't open passive connection");
        }
    }

    private void handleList(IoSession session, FtpSessionContext ctx) {
        try {
            reply(session, "150 Opening data connection for LIST");

            Socket data = openDataConnection(ctx);
            if (data == null) {
                reply(session, "425 Can't open data connection");
                return;
            }

            String[] items = fs.list(ctx.cwd);
            StringBuilder sb = new StringBuilder();
            for (String f : items) sb.append(f).append("\r\n");

            data.getOutputStream().write(sb.toString().getBytes());
            data.close();

            reply(session, "226 Transfer complete");
        } catch (Exception e) {
            reply(session, "426 Transfer aborted");
        }
    }

    private void handleRetr(IoSession session, FtpSessionContext ctx, String filename) {
        try {
            if (filename == null) {
                reply(session, "501 Missing filename");
                return;
            }

            reply(session, "150 Opening data connection for RETR");

            Socket data = openDataConnection(ctx);
            if (data == null) {
                reply(session, "425 Can't open data connection");
                return;
            }

            byte[] fileData = fs.readFile(ctx.cwd + "/" + filename);
            data.getOutputStream().write(fileData);
            data.close();

            reply(session, "226 Transfer complete");
        } catch (Exception e) {
            reply(session, "426 Transfer aborted");
        }
    }

    private void handleStor(IoSession session, FtpSessionContext ctx, String filename) {
        try {
            if (filename == null) {
                reply(session, "501 Missing filename");
                return;
            }

            reply(session, "150 Opening data connection for STOR");

            Socket data = openDataConnection(ctx);
            if (data == null) {
                reply(session, "425 Can't open data connection");
                return;
            }

            byte[] buffer = data.getInputStream().readAllBytes();
            fs.writeFile(ctx.cwd + "/" + filename, buffer);
            data.close();

            reply(session, "226 Transfer complete");
        } catch (Exception e) {
            reply(session, "426 Transfer aborted");
        }
    }

    private Socket openDataConnection(FtpSessionContext ctx) {
        try {
            if (ctx.activeDataSocket != null) return ctx.activeDataSocket;
            if (ctx.passiveDataSocket != null) return ctx.passiveDataSocket;

            if (ctx.dataHost != null) {
                return new Socket(ctx.dataHost, ctx.dataPort);
            }
        } catch (Exception ignored) {}

        return null;
    }
}