package com.example.ftpsample;

import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Thread-safe logging utility that appends messages to a TextView.
 * Optionally scrolls to bottom if inside a ScrollView.
 */
public class LogUtils {

    private final TextView textView;
    private final ScrollView scrollView; // Optional

    public LogUtils(TextView tv) {
        this(tv, null);
    }

    public LogUtils(TextView tv, ScrollView sv) {
        this.textView = tv;
        this.scrollView = sv;
    }

    /**
     * Append a log message to the TextView.
     * Runs safely on UI thread.
     */
    public void log(String msg) {
        textView.post(() -> {
            textView.append(msg + "\n");
            if (scrollView != null) {
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }

    /**
     * Clear all logs.
     */
    public void clear() {
        textView.post(() -> textView.setText(""));
    }
}