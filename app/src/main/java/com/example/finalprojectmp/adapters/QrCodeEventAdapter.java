package com.example.finalprojectmp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.models.QRCode;
import com.example.finalprojectmp.utils.EventTimeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class QrCodeEventAdapter extends RecyclerView.Adapter<QrCodeEventAdapter.QrViewHolder> {

    public interface OnQrItemClickListener {
        void onQrClick(QRCode code);
    }

    private final List<QRCode> qrCodes = new ArrayList<>();
    private final OnQrItemClickListener listener;
    private int focusedEventId = -1;

    public QrCodeEventAdapter(OnQrItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_code_event, parent, false);
        return new QrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrViewHolder holder, int position) {
        holder.bind(qrCodes.get(position));
    }

    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    public void submitList(List<QRCode> items) {
        qrCodes.clear();
        if (items != null) {
            qrCodes.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setFocusedEventId(int eventId) {
        focusedEventId = eventId;
        notifyDataSetChanged();
    }

    class QrViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventTitle;
        private final TextView eventDate;
        private final MaterialButton buttonShowQr;
        private final MaterialCardView cardView;

        QrViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            eventTitle = itemView.findViewById(R.id.textQrEventTitle);
            eventDate = itemView.findViewById(R.id.textQrEventDate);
            buttonShowQr = itemView.findViewById(R.id.buttonShowQr);
        }

        void bind(QRCode code) {
            eventTitle.setText(code.getEventTitle());
            String date = EventTimeUtils.formatDisplayDate(code.getEventDate());
            String time = EventTimeUtils.formatDisplayTime(code.getEventTime());
            if (!date.isEmpty() && !time.isEmpty()) {
                eventDate.setText(itemView.getContext().getString(R.string.event_full_datetime, date, time));
            } else if (!date.isEmpty()) {
                eventDate.setText(date);
            } else {
                eventDate.setText(time);
            }

            int highlightColor = ContextCompat.getColor(itemView.getContext(), R.color.navAccent);
            int defaultStroke = ContextCompat.getColor(itemView.getContext(), R.color.cardStroke);
            int focusedWidth = Math.round(itemView.getResources().getDimension(R.dimen.focused_card_stroke));
            int defaultWidth = Math.round(itemView.getResources().getDimension(R.dimen.default_card_stroke));
            boolean isFocused = code.getEventId() == focusedEventId;
            cardView.setStrokeColor(isFocused ? highlightColor : defaultStroke);
            cardView.setStrokeWidth(isFocused ? focusedWidth : defaultWidth);

            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onQrClick(code);
                }
            };
            itemView.setOnClickListener(clickListener);
            buttonShowQr.setOnClickListener(clickListener);
        }
    }
}
