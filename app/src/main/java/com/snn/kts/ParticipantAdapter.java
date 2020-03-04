package com.snn.kts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.Holder> implements Filterable {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Participant> participants;

    ParticipantAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.participants = participants = EventActivity.participants;
    }

    @NonNull
    @Override
    public ParticipantAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParticipantAdapter.Holder(layoutInflater.inflate(R.layout.card_participant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantAdapter.Holder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();

                if (str.isEmpty()) {
                    participants = EventActivity.participants;
                } else {
                    ArrayList<Participant> filtered = new ArrayList<>();
                    for (Participant participant : EventActivity.participants) {
                        if (participant.name.toLowerCase().startsWith(str.toLowerCase())) {
                            filtered.add(participant);
                        }
                    }
                    participants = filtered;

                    if (participants.size() == 0) {
                        mToast.showToast(context, str + " bulunamadı!");
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = participants;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                participants = (ArrayList<Participant>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class Holder extends RecyclerView.ViewHolder {
        private int position;
        private TextView participantOrder;
        private TextView participantID;
        private TextView participantName;

        Holder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, participants.get(position).name, Toast.LENGTH_SHORT).show();
                }
            });
            participantOrder = itemView.findViewById(R.id.participantOrder);
            participantID = itemView.findViewById(R.id.participantID);
            participantName = itemView.findViewById(R.id.participantName);
        }

        void setData(int position) {
            this.position = position;

            participantOrder.setText(String.valueOf(position + 1));
            participantID.setText(participants.get(position).id);
            participantName.setText(participants.get(position).name);
        }
    }
}
