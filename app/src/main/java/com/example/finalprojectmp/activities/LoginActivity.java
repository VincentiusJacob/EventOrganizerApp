package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.models.User;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        buttonLogin.setOnClickListener(v -> loginUser());
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check di db
        User user = dbHelper.getUserByEmail(email);
        if (user != null && user.verifyPassword(password)) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            SessionManager.getInstance(this).saveSession(user.getId(), user.getUserType());

            Class<?> destination = "participant".equals(user.getUserType())
                    ? ParticipantDashboardActivity.class
                    : OrganizerDashboardActivity.class;
            Intent intent = new Intent(LoginActivity.this, destination);
            intent.putExtra("USER_ID", user.getId());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
