package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.adapters.QrCodeEventAdapter;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.QRCode;

import java.util.List;

public class ParticipantQrCodesActivity extends ParticipantBaseActivity implements QrCodeEventAdapter.OnQrItemClickListener {

    public static final String EXTRA_FOCUS_EVENT_ID = "FOCUS_EVENT_ID";

    private DatabaseHelper databaseHelper;
    private QrCodeEventAdapter adapter;
    private RecyclerView recyclerView;
    private TextView textEmpty;
    private int focusEventId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_qr_codes);
        if (!ensureParticipantSession()) {
            return;
        }

        focusEventId = getIntent().getIntExtra(EXTRA_FOCUS_EVENT_ID, -1);
        databaseHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recyclerQrCodes);
        textEmpty = findViewById(R.id.textEmptyQrCodes);

        adapter = new QrCodeEventAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupNavigationBar(R.id.nav_qr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ensureParticipantSession()) {
            return;
        }
        loadQrCodes();
    }

    private void loadQrCodes() {
        List<QRCode> codes = databaseHelper.getQrCodesForUser(userId);
        adapter.submitList(codes);
        textEmpty.setVisibility(codes.isEmpty() ? View.VISIBLE : View.GONE);
        if (focusEventId > 0) {
            int targetEventId = focusEventId;
            adapter.setFocusedEventId(targetEventId);
            scrollToFocusedEvent(codes, targetEventId);
            focusEventId = -1;
        }
    }

    private void scrollToFocusedEvent(List<QRCode> codes, int targetEventId) {
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).getEventId() == targetEventId) {
                recyclerView.scrollToPosition(i);
                break;
            }
        }
    }

    @Override
    public void onQrClick(QRCode code) {
        Intent intent = new Intent(this, QrCodeDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra(QrCodeDetailActivity.EXTRA_EVENT_ID, code.getEventId());
        startActivity(intent);
    }
}
