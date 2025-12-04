package com.example.finalprojectmp.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class OrganizerScanActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    processQrPayload(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(this);

        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan Participant QR Code");
        options.setOrientationLocked(false);
        options.setBeepEnabled(true);

        qrLauncher.launch(options);
    }


    private void processQrPayload(String payload) {

        try {
            String[] parts = payload.split("\\|");
            int eventId = -1;
            int userId = -1;

            for (String part : parts) {
                if (part.startsWith("EVENT:")) {
                    eventId = Integer.parseInt(part.substring(6));
                } else if (part.startsWith("USER:")) {
                    userId = Integer.parseInt(part.substring(5));
                }
            }

            if (eventId > 0 && userId > 0) {
                String message = dbHelper.checkInUser(userId, eventId);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid QR Format", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing QR", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}