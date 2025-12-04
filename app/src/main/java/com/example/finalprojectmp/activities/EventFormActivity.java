package com.example.finalprojectmp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class EventFormActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;
    private int eventId = -1; // -1 = New Event, > 0 = Edit Mode

    private TextView textHeader;
    private TextInputEditText inputTitle, inputDate, inputTime, inputDesc;
    private Button btnSave, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        dbHelper = DatabaseHelper.getInstance(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        textHeader = findViewById(R.id.textFormHeader);
        inputTitle = findViewById(R.id.inputEventTitle);
        inputDate = findViewById(R.id.inputEventDate);
        inputTime = findViewById(R.id.inputEventTime);
        inputDesc = findViewById(R.id.inputEventDesc);
        btnSave = findViewById(R.id.btnSaveEvent);
        btnDelete = findViewById(R.id.btnDeleteEvent);

        setupPickers();

        if (eventId != -1) {
            setupEditMode();
        }

        btnSave.setOnClickListener(v -> saveEvent());

        btnDelete.setOnClickListener(v -> {
            if (dbHelper.deleteEvent(eventId)) {
                Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditMode() {
        textHeader.setText("Edit Event");
        btnSave.setText("Update Event");
        btnDelete.setVisibility(View.VISIBLE);

        Event event = dbHelper.getEventById(eventId, userId);
        if (event != null) {
            inputTitle.setText(event.getTitle());
            inputDate.setText(event.getDate());
            inputTime.setText(event.getTime());
            inputDesc.setText(event.getDescription());
        }
    }

    private void saveEvent() {
        String title = inputTitle.getText().toString().trim();
        String date = inputDate.getText().toString().trim();
        String time = inputTime.getText().toString().trim();
        String desc = inputDesc.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventId == -1) {

            long id = dbHelper.createNewEvent(userId, title, date, time, desc);
            if (id != -1) {
                Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error creating event", Toast.LENGTH_SHORT).show();
            }

        } else {
            Event event = new Event();
            event.setId(eventId);
            event.setTitle(title);
            event.setDate(date);
            event.setTime(time);
            event.setDescription(desc);

            if (dbHelper.updateEvent(event)) {
                Toast.makeText(this, "Event Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating event", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setupPickers() {
        inputDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                inputDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        inputTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                inputTime.setText(time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }
}