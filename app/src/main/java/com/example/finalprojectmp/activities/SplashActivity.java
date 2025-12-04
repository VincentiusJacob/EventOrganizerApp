package com.example.finalprojectmp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.utils.SessionManager;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // simulasi loading 2 detik
        new Handler().postDelayed(() -> {
            SessionManager sessionManager = SessionManager.getInstance(this);
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                String type = sessionManager.getUserType();
                Class<?> destination = "participant".equals(type) ? ParticipantDashboardActivity.class : OrganizerDashboardActivity.class;
                intent = new Intent(SplashActivity.this, destination);
                intent.putExtra("USER_ID", sessionManager.getUserId());
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
