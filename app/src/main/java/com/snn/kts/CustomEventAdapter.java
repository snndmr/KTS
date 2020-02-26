package com.snn.kts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CustomEventAdapter extends RecyclerView.Adapter<CustomEventAdapter.Holder> {
    private LayoutInflater layoutInflater;

    CustomEventAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CustomEventAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(layoutInflater.inflate(R.layout.card_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomEventAdapter.Holder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return MainActivity.events.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private int position;
        private TextView cardTitle;

        Holder(@NonNull View itemView) {
            super(itemView);

            cardTitle = itemView.findViewById(R.id.cardTitle);
        }

        void setData(int position) {
            this.position = position;
            cardTitle.setText(MainActivity.events.get(position).name);
        }
    }
}