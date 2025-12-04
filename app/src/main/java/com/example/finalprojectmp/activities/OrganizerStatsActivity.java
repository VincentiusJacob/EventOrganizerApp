package com.example.finalprojectmp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Attendance;
import java.util.ArrayList;
import java.util.List;

public class OrganizerStatsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spinnerEvents;
    private TextView textRsvpValue, textCheckedInValue, textAttendanceList;
    private int userId;
    private List<Event> myEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_stats);

        userId = getIntent().getIntExtra("USER_ID", -1);
        dbHelper = DatabaseHelper.getInstance(this);

        // Binding ID dari layout baru
        spinnerEvents = findViewById(R.id.spinnerStatsEvent);
        textRsvpValue = findViewById(R.id.textStatsRsvpCount);
        textCheckedInValue = findViewById(R.id.textStatsCheckedInCount);
        textAttendanceList = findViewById(R.id.textStatsList);

        loadEventsDropdown();
    }

    private void loadEventsDropdown() {
        myEvents = dbHelper.getEventsByOrganizer(userId);

        // Handle jika organizer belum punya event
        if (myEvents.isEmpty()) {
            List<String> empty = new ArrayList<>();
            empty.add("No Events Found");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, empty);
            spinnerEvents.setAdapter(adapter);
            spinnerEvents.setEnabled(false); // Matikan spinner
            return;
        }

        List<String> titles = new ArrayList<>();
        for (Event e : myEvents) titles.add(e.getTitle());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(adapter);

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStatsForEvent(myEvents.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadStatsForEvent(Event event) {
        // Ambil data terbaru dari DB
        Event freshEvent = dbHelper.getEventById(event.getId(), userId);

        // Update UI Angka (Hanya angka, karena label sudah ada di XML)
        textRsvpValue.setText(String.valueOf(freshEvent.getTotalRsvp()));
        textCheckedInValue.setText(String.valueOf(freshEvent.getTotalCheckedIn()));

        // Update Log Kehadiran
        List<Attendance> attendees = dbHelper.getEventAttendanceList(event.getId());
        StringBuilder sb = new StringBuilder();

        if(attendees.isEmpty()) {
            sb.append("Waiting for attendees to check-in...");
        } else {
            for(Attendance a : attendees) {
                // Format log yang rapi
                sb.append("✅ ").append(a.getUserName())
                        .append("\n     Time: ").append(a.getCheckInTime())
                        .append("\n\n");
            }
        }
        textAttendanceList.setText(sb.toString());
    }
}