package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.utils.SessionManager;

public abstract class ParticipantBaseActivity extends AppCompatActivity {

    protected int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId <= 0) {
            userId = SessionManager.getInstance(this).getUserId();
        }
    }

    protected boolean ensureParticipantSession() {
        if (userId <= 0) {
            Toast.makeText(this, R.string.error_missing_user_id, Toast.LENGTH_SHORT).show();
            SessionManager.getInstance(this).clearSession();
            finish();
            return false;
        }
        return true;
    }

    protected void setupNavigationBar(@IdRes int selectedNavId) {
        View navHome = findViewById(R.id.nav_home);
        if (navHome == null) {
            return;
        }

        View navEvents = findViewById(R.id.nav_events);
        View navQr = findViewById(R.id.nav_qr);
        View navProfile = findViewById(R.id.nav_profile);

        View[] navItems = {navHome, navEvents, navQr, navProfile};
        for (View navItem : navItems) {
            if (navItem != null) {
                navItem.setSelected(navItem.getId() == selectedNavId);
            }
        }

        navHome.setOnClickListener(v -> {
            if (selectedNavId != R.id.nav_home) {
                openNav(ParticipantDashboardActivity.class);
            }
        });

        if (navEvents != null) {
            navEvents.setOnClickListener(v -> {
                if (selectedNavId != R.id.nav_events) {
                    openNav(EventsListActivity.class);
                }
            });
        }

        if (navQr != null) {
            navQr.setOnClickListener(v -> {
                if (selectedNavId != R.id.nav_qr) {
                    openNav(ParticipantQrCodesActivity.class);
                }
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                if (selectedNavId != R.id.nav_profile) {
                    openNav(ParticipantProfileActivity.class);
                }
            });
        }
    }

    private void openNav(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.putExtra("USER_ID", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!getClass().equals(destination)) {
            finish();
        }
    }
}
