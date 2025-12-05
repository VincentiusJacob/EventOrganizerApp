package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Food;
import com.example.finalprojectmp.models.Rsvp;
import com.example.finalprojectmp.utils.EventTimeUtils;
import com.google.android.material.button.MaterialButton;

public class EventDetailActivity extends ParticipantBaseActivity {

    public static final String EXTRA_EVENT_ID = "EVENT_ID";

    private DatabaseHelper databaseHelper;
    private int eventId;
    private Event event;

    private TextView textTitle;
    private TextView textDateTime;
    private TextView textOrganizer;
    private TextView textDescription;
    private TextView textStats;
    private LinearLayout layoutFoodSection;
    private LinearLayout layoutFoodList;
    private MaterialButton buttonRsvp;
    private MaterialButton buttonQr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        if (!ensureParticipantSession()) {
            return;
        }
        setupNavigationBar(-1);

        databaseHelper = DatabaseHelper.getInstance(this);
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);

        textTitle = findViewById(R.id.textDetailTitle);
        textDateTime = findViewById(R.id.textDetailDateTime);
        textOrganizer = findViewById(R.id.textDetailOrganizer);
        textDescription = findViewById(R.id.textDetailDescription);
        textStats = findViewById(R.id.textDetailStats);
        layoutFoodSection = findViewById(R.id.layoutDetailFoodSection);
        layoutFoodList = findViewById(R.id.layoutDetailFoodList);
        buttonRsvp = findViewById(R.id.buttonDetailRsvp);
        buttonQr = findViewById(R.id.buttonDetailQr);

        buttonRsvp.setOnClickListener(v -> toggleRsvp());
        buttonQr.setOnClickListener(v -> openQrDetail());

        loadEvent();
    }

    private void loadEvent() {
        event = databaseHelper.getEventById(eventId, userId);
        if (event == null) {
            Toast.makeText(this, R.string.error_event_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        textTitle.setText(event.getTitle());
        textDateTime.setText(EventTimeUtils.formatFullDateTime(event));
        textOrganizer.setText(getString(R.string.event_list_organizer, event.getOrganizerName()));
        String description = event.getDescription();
        if (description == null || description.isEmpty()) {
            description = getString(R.string.event_description_empty);
        }
        textDescription.setText(description);
        textStats.setText(getString(R.string.event_attendance_stat, event.getTotalRsvp(), event.getTotalCheckedIn()));
        renderFoodMenus();

        updateButtons();
    }

    private void renderFoodMenus() {
        if (layoutFoodSection == null || layoutFoodList == null) {
            return;
        }
        layoutFoodList.removeAllViews();
        if (event == null || event.getMenus() == null || event.getMenus().isEmpty()) {
            layoutFoodSection.setVisibility(View.GONE);
            return;
        }
        layoutFoodSection.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Food food : event.getMenus()) {
            View itemView = inflater.inflate(R.layout.item_food_detail, layoutFoodList, false);
            TextView nameView = itemView.findViewById(R.id.textFoodName);
            TextView descView = itemView.findViewById(R.id.textFoodDescription);
            TextView metaView = itemView.findViewById(R.id.textFoodMeta);

            nameView.setText(food.getName());
            descView.setText(food.getDescription() == null || food.getDescription().isEmpty()
                    ? getString(R.string.food_description_placeholder)
                    : food.getDescription());

            String category = food.getCategory() == null || food.getCategory().isEmpty()
                    ? getString(R.string.food_category_unknown)
                    : food.getCategory();
            String priceLabel = food.isFree()
                    ? getString(R.string.food_price_free)
                    : String.format(getString(R.string.food_price_template), food.getPrice());
            metaView.setText(getString(R.string.food_meta_template, category, priceLabel));

            layoutFoodList.addView(itemView);
        }
    }

    private void updateButtons() {
        if (event == null) {
            return;
        }
        if (event.isUserHasRsvp()) {
            buttonRsvp.setText(R.string.button_cancel_rsvp);
            buttonQr.setVisibility(View.VISIBLE);
        } else {
            buttonRsvp.setText(R.string.button_rsvp_now);
            buttonQr.setVisibility(View.GONE);
        }
    }

    private void toggleRsvp() {
        if (event == null) {
            return;
        }
        boolean targetConfirmed = !event.isUserHasRsvp();
        String status = targetConfirmed ? Rsvp.STATUS_CONFIRMED : Rsvp.STATUS_CANCELLED;
        boolean success = databaseHelper.setRsvpStatus(userId, event.getId(), status);
        if (success) {
            Toast.makeText(this, targetConfirmed ? R.string.rsvp_successful : R.string.rsvp_cancelled, Toast.LENGTH_SHORT).show();
            loadEvent();
        } else {
            Toast.makeText(this, R.string.rsvp_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void openQrDetail() {
        if (event == null) {
            return;
        }
        Intent intent = new Intent(this, QrCodeDetailActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra(QrCodeDetailActivity.EXTRA_EVENT_ID, event.getId());
        startActivity(intent);
    }
}
