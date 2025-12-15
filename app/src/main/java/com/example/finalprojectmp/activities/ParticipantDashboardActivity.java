package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.adapters.ParticipantEventAdapter;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Participant;
import com.example.finalprojectmp.models.Rsvp;
import com.example.finalprojectmp.utils.EventStatus;
import com.example.finalprojectmp.utils.EventTimeUtils;

import org.json.JSONObject; // For parsing Weather Data

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantDashboardActivity extends ParticipantBaseActivity implements ParticipantEventAdapter.EventActionListener {

    private DatabaseHelper databaseHelper;
    private TextView textGreeting;
    private TextView textDietaryPreference;
    private TextView textUpcomingCount;
    private TextView textRsvpCount;
    private TextView textNextEventTitle;
    private TextView textNextEventDate;
    private TextView textEmptyState;
    private RecyclerView recyclerUpcoming;
    private ParticipantEventAdapter eventAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_dashboard);
        if (!ensureParticipantSession()) {
            return;
        }

        databaseHelper = DatabaseHelper.getInstance(this);

        textGreeting = findViewById(R.id.textGreeting);
        textDietaryPreference = findViewById(R.id.textDietaryPreference);
        textUpcomingCount = findViewById(R.id.textUpcomingCount);
        textRsvpCount = findViewById(R.id.textRsvpCount);
        textNextEventTitle = findViewById(R.id.textNextEventTitle);
        textNextEventDate = findViewById(R.id.textNextEventDate);
        textEmptyState = findViewById(R.id.textEmptyUpcoming);
        recyclerUpcoming = findViewById(R.id.recyclerUpcomingEvents);

        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new ParticipantEventAdapter(ParticipantEventAdapter.CardMode.DASHBOARD, this);
        recyclerUpcoming.setAdapter(eventAdapter);

        setupNavigationBar(R.id.nav_home);


        fetchWeatherData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ensureParticipantSession()) {
            return;
        }
        bindParticipantDetails();
        loadDashboardData();
    }

    private void bindParticipantDetails() {
        Participant participant = databaseHelper.getParticipantById(userId);
        if (participant == null) {
            return;
        }

        textGreeting.setText(getString(R.string.dashboard_greeting, participant.getDisplayName()));

        String dietary = participant.getDietaryPreferences();
        if (dietary == null || dietary.isEmpty()) {
            dietary = getString(R.string.dashboard_dietary_empty);
        }
        textDietaryPreference.setText(dietary);
    }


    private void fetchWeatherData() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String tempString = "";
            try {
                // API URL (Free Open-Meteo API)
                // Coordinates for default dulu (Jakarta/Indonesia).
                String urlString = "https://api.open-meteo.com/v1/forecast?latitude=-6.2088&longitude=106.8456&current_weather=true";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();


                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject currentWeather = jsonResponse.getJSONObject("current_weather");
                double temperature = currentWeather.getDouble("temperature");

                tempString = temperature + "°C";

            } catch (Exception e) {
                e.printStackTrace();
                tempString = "";
            }


            String finalTemp = tempString;
            handler.post(() -> {
                Participant participant = databaseHelper.getParticipantById(userId);
                if (participant != null && !finalTemp.isEmpty()) {

                    String currentText = getString(R.string.dashboard_greeting, participant.getDisplayName());
                    textDietaryPreference.setText(finalTemp + " • Today");
                }
            });
        });
    }


    private void loadDashboardData() {
        List<Event> allEvents = databaseHelper.getEventsForParticipant(userId);
        List<Event> upcomingEvents = new ArrayList<>();
        Event nextEvent = null;

        for (Event event : allEvents) {
            EventStatus status = EventTimeUtils.resolveStatus(event);
            if (status == EventStatus.UPCOMING || status == EventStatus.CURRENT) {
                upcomingEvents.add(event);
                if (nextEvent == null || EventTimeUtils.getEventTimestamp(event) < EventTimeUtils.getEventTimestamp(nextEvent)) {
                    nextEvent = event;
                }
            }
        }

        Collections.sort(upcomingEvents, Comparator.comparingLong(EventTimeUtils::getEventTimestamp));

        List<Event> compactList = upcomingEvents;
        if (upcomingEvents.size() > 3) {
            compactList = new ArrayList<>(upcomingEvents.subList(0, 3));
        }
        eventAdapter.submitList(new ArrayList<>(compactList));

        textUpcomingCount.setText(getString(R.string.dashboard_upcoming_label, upcomingEvents.size()));

        List<Rsvp> rsvps = databaseHelper.getRsvpsForUser(userId);
        int confirmed = 0;
        for (Rsvp rsvp : rsvps) {
            if (rsvp.isConfirmed()) {
                confirmed++;
            }
        }
        textRsvpCount.setText(String.valueOf(confirmed));

        textEmptyState.setVisibility(compactList.isEmpty() ? View.VISIBLE : View.GONE);

        if (nextEvent != null) {
            textNextEventTitle.setText(nextEvent.getTitle());
            textNextEventDate.setText(EventTimeUtils.formatFullDateTime(nextEvent));
        } else {
            textNextEventTitle.setText(R.string.dashboard_no_next_event);
            textNextEventDate.setText(R.string.dashboard_start_rsvping);
        }
    }

    @Override
    public void onEventSelected(Event event) {
        openEventDetail(event);
    }

    @Override
    public void onPrimaryAction(Event event) {
        openEventDetail(event);
    }

    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("EVENT_ID", event.getId());
        startActivity(intent);
    }
}