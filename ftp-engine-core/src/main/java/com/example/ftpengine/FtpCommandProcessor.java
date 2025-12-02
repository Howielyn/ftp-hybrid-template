package com.example.ftpengine;

import org.apache.mina.core.session.IoSession;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Processes FTP control commands.
 *
 * This class is intentionally synchronous and straightforward.
 *
 * The IoHandler should call processor.handleCommand(session, line)
 * where 'line' is a full command line including CRLF.
 */
public class FtpCommandProcessor {

    private final FtpFileSystem fs;
    private final FtpUserManager users;

    public FtpCommandProcessor(FtpFileSystem fs, FtpUserManager users) {
        this.fs = fs;
        this.users = users;
    }

    /**
     * Handle a raw command line from the control connection.
     *
     * @param line    The ASCII command line (may include CR/LF).
     * @param session The IoSession (control connection wrapper).
     */
    public void handleCommand(String line, IoSession session) {
        if (line == null) return;
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return;

        String[] parts = trimmed.split(" ", 2);
        String cmd = parts[0].toUpperCase();
        String arg = parts.length > 1 ? parts[1] : null;

        try {
            switch (cmd) {
                case "NOOP":
                    session.write("200 NOOP ok\r\n");
                    break;

                case "USER":
                    session.setAttribute("ftp.user", arg != null ? arg : "");
                    session.write("331 User name okay, need password\r\n");
                    break;

                case "PASS":
                    String username = (String) session.getAttribute("ftp.user");
                    if (username == null) username = "";
                    boolean ok = users.authenticate(username, arg != null ? arg : "");
                    if (ok) {
                        session.setAttribute("ftp.auth", true);
                        session.write("230 User logged in, proceed\r\n");
                        // set user's home dir as current dir
                        session.setAttribute("ftp.pwd", users.getHomeDir(username));
                    } else {
                        session.write("530 Not logged in\r\n");
                    }
                    break;

                case "PWD":
                    String cwd = getCwd(session);
                    session.write("257 \"" + cwd + "\" is current directory\r\n");
                    break;

                case "CWD":
                    if (arg == null) {
                        session.write("501 Syntax error in parameters or arguments\r\n");
                    } else {
                        String target = FtpUtils.resolvePath(getCwd(session), arg);
                        if (fs.isDirectory(target)) {
                            session.setAttribute("ftp.pwd", target);
                            session.write("250 Directory successfully changed.\r\n");
                        } else {
                            session.write("550 Failed to change directory.\r\n");
                        }
                    }
                    break;

                case "TYPE":
                    if ("I".equalsIgnoreCase(arg)) {
                        session.setAttribute("ftp.type", "I");
                        session.write("200 Type set to I.\r\n");
                    } else {
                        session.setAttribute("ftp.type", "A");
                        session.write("200 Type set to A.\r\n");
                    }
                    break;

                case "PASV":
                    startPassive(session);
                    break;

                case "PORT":
                    if (arg == null) {
                        session.write("501 Syntax error in parameters or arguments\r\n");
                    } else {
                        boolean connected = setupActive(session, arg);
                        if (connected) session.write("200 PORT command successful.\r\n");
                        else session.write("425 Can't open data connection.\r\n");
                    }
                    break;

                case "LIST":
                    handleLIST(session, arg);
                    break;

                case "RETR":
                    handleRETR(session, arg);
                    break;

                case "STOR":
                    handleSTOR(session, arg);
                    break;

                case "QUIT":
                    session.write("221 Goodbye\r\n");
                    try { session.close(); } catch (Exception ignored) {}
                    break;

                default:
                    session.write("502 Command not implemented\r\n");
                    break;
            }
        } catch (Exception e) {
            try { session.write("550 Internal server error\r\n"); } catch (Exception ignored) {}
        }
    }

    private String getCwd(IoSession session) {
        Object o = session.getAttribute("ftp.pwd");
        return o != null ? o.toString() : "/";
    }

    // --- LIST ---
    private void handleLIST(IoSession session, String arg) {
        try {
            String path = arg != null ? FtpUtils.resolvePath(getCwd(session), arg) : getCwd(session);
            session.write("150 Opening ASCII mode data connection for file list.\r\n");

            Socket data = openDataConnection(session);
            if (data == null) {
                session.write("425 Can't open data connection.\r\n");
                return;
            }

            PrintWriter out = new PrintWriter(new OutputStreamWriter(data.getOutputStream(), "UTF-8"), true);
            String[] list = fs.list(path);
            for (String entry : list) {
                // simple listing; you may improve with permissions/timestamps/size
                out.println(entry);
            }
            out.flush();
            data.close();

            session.write("226 Transfer complete.\r\n");
        } catch (Exception e) {
            try { session.write("426 Connection closed; transfer aborted.\r\n"); } catch (Exception ignored) {}
        } finally {
            cleanupDataConnection(session);
        }
    }

    // --- RETR ---
    private void handleRETR(IoSession session, String arg) {
        if (arg == null) {
            try { session.write("501 Missing filename\r\n"); } catch (Exception ignored) {}
            return;
        }
        try {
            String path = FtpUtils.resolvePath(getCwd(session), arg);
            if (!fs.exists(path)) {
                session.write("550 File not found\r\n");
                return;
            }
            session.write("150 Opening binary mode data connection for file transfer.\r\n");
            Socket data = openDataConnection(session);
            if (data == null) {
                session.write("425 Can't open data connection.\r\n");
                return;
            }

            try (InputStream in = fs.readFile(path);
                 OutputStream out = data.getOutputStream()) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
                out.flush();
            }

            data.close();
            session.write("226 Transfer complete.\r\n");
        } catch (Exception e) {
            try { session.write("426 Transfer aborted.\r\n"); } catch (Exception ignored) {}
        } finally {
            cleanupDataConnection(session);
        }
    }

    // --- STOR ---
    private void handleSTOR(IoSession session, String arg) {
        if (arg == null) {
            try { session.write("501 Missing filename\r\n"); } catch (Exception ignored) {}
            return;
        }
        try {
            String path = FtpUtils.resolvePath(getCwd(session), arg);
            session.write("150 Opening binary mode data connection for file upload.\r\n");

            Socket data = openDataConnection(session);
            if (data == null) {
                session.write("425 Can't open data connection.\r\n");
                return;
            }

            try (OutputStream out = fs.writeFile(path);
                 InputStream in = data.getInputStream()) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = in.read(buf)) != -1) out.write(buf, 0, r);
                out.flush();
            }
            data.close();
            session.write("226 Transfer complete.\r\n");
        } catch (Exception e) {
            try { session.write("426 Transfer aborted.\r\n"); } catch (Exception ignored) {}
        } finally {
            cleanupDataConnection(session);
        }
    }

    // --- Passive mode: open ServerSocket and store in session ---
    private void startPassive(IoSession session) {
        try {
            FtpDataConnection dataConn = new FtpDataConnection();
            dataConn.startPassive(0); // ephemeral
            int port = dataConn.getLocalPort();
            // save data connection
            session.setAttribute("ftp.data", dataConn);

            // prepare PASV response: convert local IP to comma format
            String host = InetAddress.getLocalHost().getHostAddress(); // may return 127.0.0.1 on emulator
            String[] parts = host.split("\\.");
            int p1 = port / 256;
            int p2 = port % 256;
            String pasv = String.format("227 Entering Passive Mode (%s,%s,%s,%s,%d,%d)\r\n",
                    parts[0], parts[1], parts[2], parts[3], p1, p2);
            session.write(pasv);
        } catch (Exception e) {
            try { session.write("425 Can't open passive connection\r\n"); } catch (Exception ignored) {}
        }
    }

    // --- Active mode (PORT): parse host, connect from server to client ---
    private boolean setupActive(IoSession session, String arg) {
        try {
            String[] nums = arg.split(",");
            if (nums.length != 6) return false;
            String host = String.join(".", nums[0], nums[1], nums[2], nums[3]);
            int port = Integer.parseInt(nums[4]) * 256 + Integer.parseInt(nums[5]);

            FtpDataConnection conn = new FtpDataConnection();
            conn.connectActive(host, port);
            session.setAttribute("ftp.data.active", conn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Open data connection (either passive accept or active socket)
    private Socket openDataConnection(IoSession session) throws Exception {
        // Active (server connects to client)
        FtpDataConnection active = (FtpDataConnection) session.getAttribute("ftp.data.active");
        if (active != null && active.isConnected()) {
            return active.getSocket();
        }

        // Passive: accept connection on server's pasv socket
        FtpDataConnection pasv = (FtpDataConnection) session.getAttribute("ftp.data");
        if (pasv != null) {
            Socket s = pasv.accept();
            return s;
        }

        // no data connection available
        return null;
    }

    private void cleanupDataConnection(IoSession session) {
        try {
            FtpDataConnection pasv = (FtpDataConnection) session.getAttribute("ftp.data");
            if (pasv != null) { pasv.stop(); session.setAttribute("ftp.data", null); }
        } catch (Exception ignored) {}
        try {
            FtpDataConnection active = (FtpDataConnection) session.getAttribute("ftp.data.active");
            if (active != null) { active.stop(); session.setAttribute("ftp.data.active", null); }
        } catch (Exception ignored) {}
    }
}