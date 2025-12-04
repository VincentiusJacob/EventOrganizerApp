package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Organizer;
import com.example.finalprojectmp.utils.SessionManager;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private int userId;
    private DatabaseHelper dbHelper;
    private TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        dbHelper = DatabaseHelper.getInstance(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) userId = SessionManager.getInstance(this).getUserId();

        textWelcome = findViewById(R.id.textOrgWelcome);
        TextView btnLogout = findViewById(R.id.btnOrgLogout);

        CardView cardManage = findViewById(R.id.cardManageEvents);
        CardView cardScan = findViewById(R.id.cardScanQr);
        CardView cardStats = findViewById(R.id.cardEventStats);

        cardManage.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerManageEventsActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        cardScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerScanActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        cardStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerStatsActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(this).clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadOrganizerData();
    }

    private void loadOrganizerData() {
        Organizer org = dbHelper.getOrganizerById(userId);
        if (org != null) {
            textWelcome.setText("Welcome, " + org.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizerData();
    }
}