package com.example.sampleapp;

import android.widget.TextView;

/**
 * Simple thread-safe logging utility to append logs to TextView.
 */
public class LogUtils {

    private final TextView textView;

    public LogUtils(TextView tv) {
        this.textView = tv;
    }

    public void log(String msg) {
        textView.post(() -> {
            textView.append(msg + "\n");
        });
    }
}