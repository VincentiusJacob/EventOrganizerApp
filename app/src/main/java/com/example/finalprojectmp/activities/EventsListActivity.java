package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.adapters.ParticipantEventAdapter;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Rsvp;
import com.example.finalprojectmp.utils.EventStatus;
import com.example.finalprojectmp.utils.EventTimeUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class EventsListActivity extends ParticipantBaseActivity implements ParticipantEventAdapter.EventActionListener {

    private DatabaseHelper databaseHelper;
    private ParticipantEventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private TextView textEmpty;
    private ChipGroup chipGroup;
    private final List<Event> allEvents = new ArrayList<>();
    private EventStatus currentFilter = EventStatus.UPCOMING;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        if (!ensureParticipantSession()) {
            return;
        }

        databaseHelper = DatabaseHelper.getInstance(this);

        recyclerView = findViewById(R.id.recyclerEvents);
        textEmpty = findViewById(R.id.textEmptyEvents);
        chipGroup = findViewById(R.id.chipStatusGroup);

        eventAdapter = new ParticipantEventAdapter(ParticipantEventAdapter.CardMode.FULL, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        setupNavigationBar(R.id.nav_events);

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null && chip.getTag() instanceof EventStatus) {
                currentFilter = (EventStatus) chip.getTag();
                filterEvents();
            }
        });

        initializeChipTags();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ensureParticipantSession()) {
            return;
        }
        loadEventsFromDatabase();
    }

    private void initializeChipTags() {
        Chip chipPast = chipGroup.findViewById(R.id.chipPast);
        Chip chipCurrent = chipGroup.findViewById(R.id.chipCurrent);
        Chip chipUpcoming = chipGroup.findViewById(R.id.chipUpcoming);
        if (chipPast != null) {
            chipPast.setTag(EventStatus.PAST);
        }
        if (chipCurrent != null) {
            chipCurrent.setTag(EventStatus.CURRENT);
        }
        if (chipUpcoming != null) {
            chipUpcoming.setTag(EventStatus.UPCOMING);
        }
        checkChipForStatus(EventStatus.UPCOMING);
    }

    private void loadEventsFromDatabase() {
        allEvents.clear();
        allEvents.addAll(databaseHelper.getEventsForParticipant(userId));
        filterEvents();
    }

    private void filterEvents() {
        List<Event> filtered = filterEventsByStatus(currentFilter);
        if (filtered.isEmpty() && !allEvents.isEmpty()) {
            EventStatus fallback = findFirstAvailableStatus();
            if (fallback != null && fallback != currentFilter) {
                currentFilter = fallback;
                checkChipForStatus(fallback);
                return;
            }
        }
        eventAdapter.submitList(filtered);
        textEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private List<Event> filterEventsByStatus(EventStatus status) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : allEvents) {
            if (EventTimeUtils.resolveStatus(event) == status) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    private EventStatus findFirstAvailableStatus() {
        EventStatus[] priority = new EventStatus[]{EventStatus.CURRENT, EventStatus.UPCOMING, EventStatus.PAST};
        for (EventStatus status : priority) {
            if (!filterEventsByStatus(status).isEmpty()) {
                return status;
            }
        }
        return null;
    }

    private void checkChipForStatus(EventStatus status) {
        if (chipGroup == null) {
            return;
        }
        int chipId = getChipIdForStatus(status);
        if (chipId != View.NO_ID && chipGroup.getCheckedChipId() != chipId) {
            chipGroup.check(chipId);
        }
    }

    private int getChipIdForStatus(EventStatus status) {
        if (status == null) {
            return View.NO_ID;
        }
        switch (status) {
            case PAST:
                return R.id.chipPast;
            case CURRENT:
                return R.id.chipCurrent;
            case UPCOMING:
                return R.id.chipUpcoming;
            default:
                return View.NO_ID;
        }
    }

    @Override
    public void onEventSelected(Event event) {
        openEventDetail(event.getId());
    }

    @Override
    public void onPrimaryAction(Event event) {
        if (event.isUserHasRsvp()) {
            Intent intent = new Intent(this, ParticipantQrCodesActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra(ParticipantQrCodesActivity.EXTRA_FOCUS_EVENT_ID, event.getId());
            startActivity(intent);
        } else {
            boolean success = databaseHelper.setRsvpStatus(userId, event.getId(), Rsvp.STATUS_CONFIRMED);
            if (success) {
                Snackbar.make(recyclerView, R.string.rsvp_successful, Snackbar.LENGTH_SHORT).show();
                loadEventsFromDatabase();
            } else {
                Snackbar.make(recyclerView, R.string.rsvp_failed, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void openEventDetail(int eventId) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
    }
}
