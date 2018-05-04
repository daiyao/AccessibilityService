package com.dy.accessibilityservicedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BIND_ACCESSIBILITY_SERVICE)
                == PackageManager.PERMISSION_GRANTED) {
            startAccessibilityService();
        } else {
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BIND_ACCESSIBILITY_SERVICE},
                    10001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 10001:
                Toast.makeText(this, "获得权限", Toast.LENGTH_SHORT).show();
                startAccessibilityService();
                break;
            default:
                break;
        }
    }

    private void startAccessibilityService() {
        Intent accessibilityService = new Intent(this, MyAccessibilityService.class);
        startService(accessibilityService);
    }
}
