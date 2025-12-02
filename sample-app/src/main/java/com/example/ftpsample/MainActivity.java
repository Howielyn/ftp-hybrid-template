package com.example.ftpsample;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ftp.FtpHybridServer;
import java.io.File;

/**
 * Minimal sample activity that starts/stops the hybrid server.
 * WARNING: This is a simple example; proper permission checks are required.
 */
public class MainActivity extends AppCompatActivity {
    private FtpHybridServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        Button startBtn = new Button(this);
        startBtn.setText("Start FTP Server (2121)");
        Button stopBtn = new Button(this);
        stopBtn.setText("Stop FTP Server");

        layout.addView(startBtn);
        layout.addView(stopBtn);
        setContentView(layout);

        startBtn.setOnClickListener(v -> {
            try {
                File root = Environment.getExternalStorageDirectory();
                server = new FtpHybridServer(root, 2121);
                server.start();
                Toast.makeText(this, "Server started on port 2121", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error starting server: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        stopBtn.setOnClickListener(v -> {
            if (server != null) {
                server.stop();
                Toast.makeText(this, "Server stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
