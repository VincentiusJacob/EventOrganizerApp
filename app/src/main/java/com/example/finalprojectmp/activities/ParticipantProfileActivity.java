package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Participant;
import com.example.finalprojectmp.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ParticipantProfileActivity extends ParticipantBaseActivity {

    private DatabaseHelper databaseHelper;
    private TextView textName;
    private TextView textEmail;
    private TextInputEditText editDietary;
    private TextInputEditText editInterests;
    private Switch switchEmail;
    private Switch switchSms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_profile);
        if (!ensureParticipantSession()) {
            return;
        }

        databaseHelper = DatabaseHelper.getInstance(this);

        textName = findViewById(R.id.textProfileName);
        textEmail = findViewById(R.id.textProfileEmail);
        editDietary = findViewById(R.id.editDietaryPreference);
        editInterests = findViewById(R.id.editInterests);
        switchEmail = findViewById(R.id.switchEmailNotif);
        switchSms = findViewById(R.id.switchSmsNotif);
        MaterialButton buttonSave = findViewById(R.id.buttonSaveProfile);
        MaterialButton buttonLogout = findViewById(R.id.buttonLogout);

        buttonSave.setOnClickListener(v -> saveProfile());
        buttonLogout.setOnClickListener(v -> logout());

        setupNavigationBar(R.id.nav_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ensureParticipantSession()) {
            return;
        }
        loadProfile();
    }

    private void loadProfile() {
        Participant participant = databaseHelper.getParticipantById(userId);
        if (participant == null) {
            return;
        }
        textName.setText(participant.getDisplayName());
        textEmail.setText(participant.getEmail());
        editDietary.setText(participant.getDietaryPreferences());
        editInterests.setText(participant.getInterests());
        switchEmail.setChecked(participant.isEmailNotifications());
        switchSms.setChecked(participant.isSmsNotifications());
    }

    private void saveProfile() {
        String dietary = editDietary.getText() != null ? editDietary.getText().toString().trim() : "";
        String interests = editInterests.getText() != null ? editInterests.getText().toString().trim() : "";
        boolean success = databaseHelper.updateParticipantProfile(userId, dietary, interests,
                switchEmail.isChecked(), switchSms.isChecked());
        if (success) {
            Toast.makeText(this, R.string.profile_dietary_updated, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.profile_dietary_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        SessionManager.getInstance(this).clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
