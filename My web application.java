// MainActivity.java
package com.example.privacymodeapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 100;
    private TextView imeiText, privateDataText;
    private EditText passwordInput;
    private Button hackAttemptBtn, unlockBtn;
    private String correctPassword = "1234";  // user privacy password
    private String imeiNumber = "Unknown";
    private boolean privacyMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imeiText = findViewById(R.id.imeiText);
        privateDataText = findViewById(R.id.privateDataText);
        passwordInput = findViewById(R.id.passwordInput);
        hackAttemptBtn = findViewById(R.id.hackAttemptBtn);
        unlockBtn = findViewById(R.id.unlockBtn);

        requestPermissions();

        hackAttemptBtn.setOnClickListener(v -> {
            activatePrivacyMode();
            sendAlertMessage();
        });

        unlockBtn.setOnClickListener(v -> {
            String entered = passwordInput.getText().toString();
            if (entered.equals(correctPassword)) {
                disablePrivacyMode();
            } else {
                Toast.makeText(this, "Wrong Password!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS},
                PERMISSION_REQUEST);
    }

    private void activatePrivacyMode() {
        privacyMode = true;
        privateDataText.setText("🔒 Data Hidden (Privacy Mode Active)");
        Toast.makeText(this, "Privacy Mode Activated!", Toast.LENGTH_SHORT).show();
    }

    private void disablePrivacyMode() {
        privacyMode = false;
        privateDataText.setText("Your Secured Data: [Bank PIN, Notes, etc]");
        Toast.makeText(this, "Privacy Mode Disabled!", Toast.LENGTH_SHORT).show();
    }

    private void sendAlertMessage() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    "9999999999", // emergency number (replace with your own)
                    null,
                    "⚠ Alert! Someone tried to hack your phone. IMEI: " + imeiNumber,
                    null,
                    null
            );
            Toast.makeText(this, "Alert Message Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEI();
            } else {
                imeiText.setText("Permission Denied!");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getIMEI() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                imeiNumber = tm.getImei(); // For Android 10+, use getMeid()/getDeviceId()
                imeiText.setText("IMEI: " + imeiNumber);
            }
        } catch (Exception e) {
            imeiText.setText("IMEI not available");
        }
    }
}
