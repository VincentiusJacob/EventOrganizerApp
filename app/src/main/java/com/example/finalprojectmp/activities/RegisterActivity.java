package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.models.Participant;
import com.example.finalprojectmp.models.Organizer;
import com.example.finalprojectmp.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone;

    // Changed: No RadioGroup, define RadioButtons directly
    private RadioButton rbParticipant, rbOrganizer;

    private Button buttonRegister;
    private TextView textViewLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = DatabaseHelper.getInstance(this);

        // Init Inputs
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);

        // Init Radio Buttons
        rbParticipant = findViewById(R.id.radioButtonParticipant);
        rbOrganizer = findViewById(R.id.radioButtonOrganizer);

        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // --- MANUAL MUTUAL EXCLUSION LOGIC ---
        // This makes sure only one button can be checked at a time
        rbParticipant.setOnClickListener(v -> {
            if (rbParticipant.isChecked()) {
                rbOrganizer.setChecked(false);
            }
        });

        rbOrganizer.setOnClickListener(v -> {
            if (rbOrganizer.isChecked()) {
                rbParticipant.setChecked(false);
            }
        });
        // -------------------------------------

        buttonRegister.setOnClickListener(v -> registerUser());
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- MANUAL VALIDATION LOGIC ---
        String userType = "";
        if (rbParticipant.isChecked()) {
            userType = "participant";
        } else if (rbOrganizer.isChecked()) {
            userType = "organizer";
        } else {
            // If neither is checked
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (dbHelper.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert user by type
        long userId = -1;
        if ("participant".equals(userType)) {
            Participant participant = new Participant(name, email, password, phone);
            userId = dbHelper.insertParticipant(participant);
        } else if ("organizer".equals(userType)) {
            Organizer organizer = new Organizer(name, email, password, phone);
            organizer.setOrganizationName("Default Org"); // Consider asking this in a next step
            organizer.setOrganizationType("individual");
            userId = dbHelper.insertOrganizer(organizer);
        }

        if (userId > 0) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }
}