package com.snn.kts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    private Toast toast;
    private int position;
    private CustomParticipantAdapter participantAdapter;
    private ArrayList<Participant> participants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        position = getIntent().getIntExtra("Position", 0);
        if (MainActivity.events.get(position).participants != null)
            participants.addAll(MainActivity.events.get(position).participants);
        initComponents();
    }

    private void initComponents() {
        RecyclerView rvParticipants = findViewById(R.id.rvParticipant);
        participantAdapter = new CustomParticipantAdapter(EventActivity.this, participants);

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

        final Event event = MainActivity.events.get(position);

        tvEventName.setText(event.name);
        tvEventDate.setText(event.date);
        tvEventLocation.setText(event.location);
        tvEventDescription.setText(event.description);

        final AppBarLayout appBar = findViewById(R.id.app_bar);

        EditText etSearch = findViewById(R.id.etSearchBar);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBar.setExpanded(false);
                if (fabScan.getVisibility() != View.VISIBLE)
                    fabScan.show();
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                participants.clear();
                if (MainActivity.events.get(position).participants != null) {
                    for (Participant participant : MainActivity.events.get(position).participants) {
                        if (participant.name.startsWith(String.valueOf(s)) ||
                                participant.name.toLowerCase().startsWith(String.valueOf(s))) {
                            participants.add(participant);
                        }
                    }
                    if (participants.size() == 0) {
                        showToast(s + " bulunamadı!");
                        participants.addAll(MainActivity.events.get(position).participants);
                    }
                    participantAdapter.notifyDataSetChanged();
                } else {
                    showToast(s + " bulunamadı!");
                }

                if (fabScan.getVisibility() != View.VISIBLE)
                    fabScan.show();
            }
        });
    }

    private void showToast(String text) {
        if (this.toast != null) {
            this.toast.cancel();
        }
        this.toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        this.toast.show();
    }
}
