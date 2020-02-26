package com.snn.kts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventActivity extends AppCompatActivity {

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        position = getIntent().getIntExtra("Position", 0);

        initComponents();
    }

    private void initComponents() {
        FloatingActionButton fabScan = findViewById(R.id.fabScan);
        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventActivity.this.startActivity(new Intent(EventActivity.this, ScannerActivity.class));
            }
        });

        TextView tvEventName = findViewById(R.id.tvEventName);
        TextView tvEventDate = findViewById(R.id.tvEventDate);
        TextView tvEventLocation = findViewById(R.id.tvEventLocation);
        TextView tvEventDescription = findViewById(R.id.tvEventDescription);

        Event event = MainActivity.events.get(position);

        tvEventName.setText(event.name);
        tvEventDate.setText(event.date);
        tvEventLocation.setText(event.location);
        tvEventDescription.setText(event.description);
    }
}
