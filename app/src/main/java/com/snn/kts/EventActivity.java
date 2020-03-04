package com.snn.kts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    static ArrayList<Participant> participants = new ArrayList<>();
    private int position;
    private ParticipantAdapter participantAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        position = getIntent().getIntExtra("Position", 0);
        initComponents();
    }

    private void initComponents() {
        participants.addAll(EventAdapter.events.get(position).participants);

        RecyclerView rvParticipants = findViewById(R.id.rvParticipant);
        participantAdapter = new ParticipantAdapter(EventActivity.this);

        rvParticipants.setAdapter(participantAdapter);
        rvParticipants.setLayoutManager(new LinearLayoutManager(EventActivity.this));

        final FloatingActionButton fabScan = findViewById(R.id.fabScan);
        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventActivity.this.startActivity(new Intent(EventActivity.this, ScannerActivity.class));
            }
        });

        rvParticipants.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabScan.getVisibility() == View.VISIBLE) {
                    fabScan.hide();
                } else if (dy < 0 && fabScan.getVisibility() != View.VISIBLE) {
                    fabScan.show();
                }
            }
        });

        TextView tvEventName = findViewById(R.id.tvEventName);
        TextView tvEventDate = findViewById(R.id.tvEventDate);
        TextView tvEventLocation = findViewById(R.id.tvEventLocation);
        TextView tvEventDescription = findViewById(R.id.tvEventDescription);

        final Event event = EventAdapter.events.get(position);

        tvEventName.setText(event.name);
        tvEventDate.setText(event.date);
        tvEventLocation.setText(event.location);
        tvEventDescription.setText(event.description);

        final AppBarLayout appBar = findViewById(R.id.app_bar);

        /* Search Part */
        EditText etSearch = findViewById(R.id.etSearchBar);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBar.setExpanded(false);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                participantAdapter.getFilter().filter(s);
                if (fabScan.getVisibility() != View.VISIBLE)
                    fabScan.show();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /* End of Search Part */
    }
}
