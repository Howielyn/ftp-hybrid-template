package com.example.ftpengine;

/**
 * Utility helpers for FTP path handling.
 */
public class FtpUtils {

    public static String normalize(String p) {
        if (p == null || p.isEmpty()) return "/";
        // collapse repeated slashes
        p = p.replaceAll("/{2,}", "/");
        if (!p.startsWith("/")) p = "/" + p;
        // remove trailing slash except for root
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    public static String resolvePath(String cwd, String arg) {
        if (arg == null || arg.isEmpty()) return normalize(cwd);
        if (arg.startsWith("/")) return normalize(arg);
        String base = normalize(cwd);
        if (base.equals("/")) return normalize("/" + arg);
        return normalize(base + "/" + arg);
    }
}