package com.snn.kts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Event> events = new ArrayList<>();

    private FloatingActionButton fabAddEvent;
    private DatabaseReference databaseReference;
    private CustomEventAdapter customEventAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        readEvents();
    }

    private void initComponents() {
        fabAddEvent = findViewById(R.id.fabAddEvent);
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        customEventAdapter = new CustomEventAdapter(MainActivity.this);

        rvEvents.setAdapter(customEventAdapter);
        rvEvents.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
    }

    private void readEvents() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    for (DataSnapshot child : children.getChildren()) {
                        MainActivity.events.add(0, child.getValue(Event.class));
                    }
                }
                customEventAdapter.notifyDataSetChanged();

                if (shimmerFrameLayout.getVisibility() != View.GONE) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    fabAddEvent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void createEvent(Event event) {
        databaseReference.child("events").child(String.valueOf(event.id)).setValue(event);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.event_add_dialog);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        Event event = new Event();
        event.date = date;
        event.time = time;

        final EditText etEventName = dialog.findViewById(R.id.etEventName);
        final EditText etEventLocation = dialog.findViewById(R.id.etEventLocation);
        final EditText etEventDescription = dialog.findViewById(R.id.etEventDescription);
        final TextView tvDate = dialog.findViewById(R.id.tvDate);
        final TextView tvTime = dialog.findViewById(R.id.tvTime);
        TextView tvAddEvent = dialog.findViewById(R.id.tvEventAdd);

        tvDate.setText(date);
        tvTime.setText(time);

        // Date Section
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvDate.setText(date);
                    }
                });
                datePickerDialog.show();
            }
        });

        // Time Section
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCalendar = Calendar.getInstance();

                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = mCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                tvTime.setText(time);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        tvAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEventName.getText().length() > 0) {
                    createEvent(new Event(
                            String.valueOf(System.currentTimeMillis()),
                            etEventName.getText().toString(),
                            tvDate.getText().toString(),
                            tvTime.getText().toString(),
                            etEventLocation.getText().toString(),
                            etEventDescription.getText().toString()));
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Etkinlik adını girin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
