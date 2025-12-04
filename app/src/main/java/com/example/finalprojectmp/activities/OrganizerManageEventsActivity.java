package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalprojectmp.R;
import com.example.finalprojectmp.adapters.OrganizerEventAdapter;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class OrganizerManageEventsActivity extends AppCompatActivity implements OrganizerEventAdapter.OnEventActionListener {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private OrganizerEventAdapter adapter;
    private TextView textEmpty;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_manage_events);

        userId = getIntent().getIntExtra("USER_ID", -1);
        dbHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recyclerOrgEvents);
        textEmpty = findViewById(R.id.textOrgEmptyEvents);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddEvent);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventFormActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        List<Event> events = dbHelper.getEventsByOrganizer(userId);

        adapter = new OrganizerEventAdapter(events, this);
        recyclerView.setAdapter(adapter);

        textEmpty.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEditEvent(Event event) {
        Intent intent = new Intent(this, EventFormActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }
}