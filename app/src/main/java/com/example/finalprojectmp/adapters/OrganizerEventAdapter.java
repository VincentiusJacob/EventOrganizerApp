package com.example.finalprojectmp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finalprojectmp.R;
import com.example.finalprojectmp.models.Event;
import java.util.List;

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.OrgViewHolder> {

    private List<Event> eventList;
    private final OnEventActionListener listener;

    public interface OnEventActionListener {
        void onEditEvent(Event event);
    }

    public OrganizerEventAdapter(List<Event> eventList, OnEventActionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_organizer, parent, false);
        return new OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.textTitle.setText(event.getTitle());
        holder.textDate.setText(event.getDate() + " • " + event.getTime());
        holder.textRsvp.setText(event.getTotalRsvp() + " Registrations");

        holder.btnEdit.setOnClickListener(v -> listener.onEditEvent(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textRsvp;
        Button btnEdit;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textEventTitle);
            textDate = itemView.findViewById(R.id.textEventDate);
            textRsvp = itemView.findViewById(R.id.textRsvpCount);
            btnEdit = itemView.findViewById(R.id.btnEditEvent);
        }
    }
}