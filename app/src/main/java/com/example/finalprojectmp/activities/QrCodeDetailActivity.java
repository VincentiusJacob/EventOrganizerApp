package com.example.finalprojectmp.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.QRCode;
import com.example.finalprojectmp.utils.EventTimeUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeDetailActivity extends ParticipantBaseActivity {

    public static final String EXTRA_EVENT_ID = "EVENT_ID";

    private DatabaseHelper databaseHelper;
    private ImageView imageQr;
    private TextView textTitle;
    private TextView textDate;
    private TextView textPayload;
    private int eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_detail);
        if (!ensureParticipantSession()) {
            return;
        }
        setupNavigationBar(-1);

        databaseHelper = DatabaseHelper.getInstance(this);
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);

        imageQr = findViewById(R.id.imageQrCode);
        textTitle = findViewById(R.id.textQrTitle);
        textDate = findViewById(R.id.textQrDate);
        textPayload = findViewById(R.id.textQrPayload);

        loadQrCode();
    }

    private void loadQrCode() {
        QRCode qrCode = databaseHelper.getOrCreateQrCode(userId, eventId);
        if (qrCode == null) {
            Toast.makeText(this, R.string.error_qr_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textTitle.setText(qrCode.getEventTitle());
        String date = EventTimeUtils.formatDisplayDate(qrCode.getEventDate());
        String time = EventTimeUtils.formatDisplayTime(qrCode.getEventTime());
        if (!date.isEmpty() && !time.isEmpty()) {
            textDate.setText(getString(R.string.event_full_datetime, date, time));
        } else if (!date.isEmpty()) {
            textDate.setText(date);
        } else {
            textDate.setText(time);
        }
        textPayload.setText(qrCode.getCodeText());

        renderQrCode(qrCode.getCodeText());
    }

    private void renderQrCode(String payload) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(payload, BarcodeFormat.QR_CODE, 768, 768);
            Bitmap bitmap = Bitmap.createBitmap(matrix.getWidth(), matrix.getHeight(), Bitmap.Config.RGB_565);
            for (int x = 0; x < matrix.getWidth(); x++) {
                for (int y = 0; y < matrix.getHeight(); y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            imageQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, R.string.error_qr_generation, Toast.LENGTH_SHORT).show();
        }
    }
}
