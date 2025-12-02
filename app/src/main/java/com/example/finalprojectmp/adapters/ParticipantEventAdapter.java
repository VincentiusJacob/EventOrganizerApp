package com.example.finalprojectmp.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.utils.EventStatus;
import com.example.finalprojectmp.utils.EventTimeUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ParticipantEventAdapter extends RecyclerView.Adapter<ParticipantEventAdapter.EventViewHolder> {

    public enum CardMode {
        DASHBOARD,
        FULL
    }

    public interface EventActionListener {
        void onEventSelected(Event event);

        void onPrimaryAction(Event event);
    }

    private final List<Event> events = new ArrayList<>();
    private final EventActionListener listener;
    private final CardMode mode;

    public ParticipantEventAdapter(CardMode mode, EventActionListener listener) {
        this.mode = mode;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void submitList(List<Event> data) {
        events.clear();
        if (data != null) {
            events.addAll(data);
        }
        notifyDataSetChanged();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView dateTime;
        private final TextView organizer;
        private final TextView attendance;
        private final TextView statusChip;
        private final MaterialButton primaryButton;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textEventTitle);
            dateTime = itemView.findViewById(R.id.textEventDateTime);
            organizer = itemView.findViewById(R.id.textEventOrganizer);
            attendance = itemView.findViewById(R.id.textEventAttendance);
            statusChip = itemView.findViewById(R.id.textEventStatus);
            primaryButton = itemView.findViewById(R.id.buttonPrimaryAction);
        }

        void bind(Event event) {
            title.setText(event.getTitle());
            dateTime.setText(EventTimeUtils.formatFullDateTime(event));
            String organizerName = event.getOrganizerName() != null ? event.getOrganizerName() : itemView.getContext().getString(R.string.label_unknown_organizer);
            organizer.setText(itemView.getContext().getString(R.string.event_list_organizer, organizerName));
            attendance.setText(itemView.getContext().getString(R.string.event_attendance_stat,
                    event.getTotalRsvp(), event.getTotalCheckedIn()));

            EventStatus status = EventTimeUtils.resolveStatus(event);
            statusChip.setText(status.getLabel());
            int colorRes;
            switch (status) {
                case PAST:
                    colorRes = R.color.statusPast;
                    break;
                case CURRENT:
                    colorRes = R.color.statusCurrent;
                    break;
                default:
                    colorRes = R.color.statusUpcoming;
                    break;
            }
            int color = ContextCompat.getColor(itemView.getContext(), colorRes);
            ViewCompat.setBackgroundTintList(statusChip, ColorStateList.valueOf(color));
            statusChip.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            if (mode == CardMode.FULL) {
                primaryButton.setVisibility(View.VISIBLE);
                if (event.isUserHasRsvp()) {
                    primaryButton.setText(R.string.button_view_qr);
                } else {
                    primaryButton.setText(R.string.button_rsvp_now);
                }
            } else {
                primaryButton.setVisibility(View.VISIBLE);
                primaryButton.setText(R.string.button_view_details);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventSelected(event);
                }
            });

            primaryButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPrimaryAction(event);
                }
            });
        }
    }
}
