package com.example.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ftpengine.filesystem.saf.SAFFileSystem;
import com.example.ftphybrid.FtpEngineHybrid;
import com.example.ftphybrid.AndroidUtils;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_OPEN_TREE = 1001;

    private SAFFileSystem safFs;
    private FtpEngineHybrid ftpEngine;
    private LogUtils logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtLog = findViewById(R.id.txtLog);
        logger = new LogUtils(txtLog);

        Button btnChooseFolder = findViewById(R.id.btnChooseFolder);
        Button btnStart = findViewById(R.id.btnStartServer);
        Button btnStop = findViewById(R.id.btnStopServer);

        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = AndroidUtils.requestSAFRootFolder();
            startActivityForResult(intent, REQUEST_CODE_OPEN_TREE);
        });

        btnStart.setOnClickListener(v -> {
            if (safFs == null) {
                Toast.makeText(this, "Please choose a folder first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ftpEngine != null) {
                Toast.makeText(this, "FTP Server already running", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                ftpEngine = new FtpEngineHybrid(this, safFs);
                ftpEngine.start(2121);
                logger.log("FTP Server started on port 2121");
            } catch (Exception e) {
                logger.log("Error starting FTP Server: " + e.getMessage());
            }
        });

        btnStop.setOnClickListener(v -> {
            if (ftpEngine != null) {
                ftpEngine.stop();
                ftpEngine = null;
                logger.log("FTP Server stopped");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPEN_TREE && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                AndroidUtils.takePersistablePermission(this, treeUri);
                safFs = new SAFFileSystem(this, treeUri);
                logger.log("Selected folder: " + treeUri.getPath());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}