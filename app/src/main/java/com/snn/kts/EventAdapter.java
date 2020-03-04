package com.snn.kts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class EventAdapter extends RecyclerView.Adapter<EventAdapter.Holder> implements Filterable {

    static ArrayList<Event> events;
    private Context context;
    private LayoutInflater layoutInflater;

    EventAdapter(Context context) {
        this.context = context;
        events = MainActivity.events;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(layoutInflater.inflate(R.layout.card_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.Holder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();

                if (str.isEmpty()) {
                    events = MainActivity.events;
                } else {
                    ArrayList<Event> filtered = new ArrayList<>();
                    for (Event event : MainActivity.events) {
                        if (event.name.toLowerCase().startsWith(str.toLowerCase())) {
                            filtered.add(event);
                        }
                    }
                    events = filtered;

                    if (events.size() == 0) {
                        mToast.showToast(context, str + " bulunamadı!");
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = events;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                events = (ArrayList<Event>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class Holder extends RecyclerView.ViewHolder {
        private int position;
        private TextView cardTitle;

        Holder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("Position", position);
                    context.startActivity(intent);
                }
            });
            cardTitle = itemView.findViewById(R.id.cardTitle);
        }

        void setData(int position) {
            this.position = position;
            cardTitle.setText(events.get(position).name);
        }
    }
}