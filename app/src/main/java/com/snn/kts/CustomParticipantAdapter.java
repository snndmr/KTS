package com.snn.kts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomParticipantAdapter extends RecyclerView.Adapter<CustomParticipantAdapter.Holder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Participant> participants;

    CustomParticipantAdapter(Context context, ArrayList<Participant> participants) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.participants = participants;
    }

    @NonNull
    @Override
    public CustomParticipantAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomParticipantAdapter.Holder(layoutInflater.inflate(R.layout.card_participant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomParticipantAdapter.Holder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return participants.size();
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
