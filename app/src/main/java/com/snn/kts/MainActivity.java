package com.snn.kts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Event> events = new ArrayList<>();

    private FloatingActionButton fabAddEvent;
    private CustomEventAdapter customEventAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private DatabaseReference databaseEventReference;

    /*private void createTestVal() {
        for (int i = 0; i < 10; i++) {
            ArrayList<Participant> participants = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                participants.add(new Participant(
                        String.valueOf(System.currentTimeMillis()) + j,
                        "participant " + j + " : " + i,
                        "participant " + j + " : " + i + "@ybu.edu.tr",
                        "555 " + i + "23" + j + "4 " + i));
            }

            for (int j = 0; j < 30; j++) {
                participants.add(new Student(
                        String.valueOf(System.currentTimeMillis()) + j,
                        "student " + j + " : " + i,
                        "student " + j + " : " + i + "@ybu.edu.tr",
                        "555 " + i + "23" + j + "4 " + i,
                        "Computer Engineering" + j,
                        "555 " + i + "23" + j + "4 " + i));
            }

            Event event = new Event(
                    String.valueOf(System.currentTimeMillis()) + i,
                    "event " + i,
                    i % 31 + "/" + i % 12 + "/2020",
                    i % 24 + ":" + i,
                    "Ayvalı, Gazze Cd. No:7, 06010 Keçiören/Ankara",
                    "Kariyer Günleri " + i,
                    participants);
            createEvent(event);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        //createTestVal();
        readEvents();
    }

    private void initComponents() {
        databaseEventReference = FirebaseDatabase.getInstance().getReference("events");

        final AppBarLayout appBar = findViewById(R.id.app_bar);

        fabAddEvent = findViewById(R.id.fabAddEvent);
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        customEventAdapter = new CustomEventAdapter(MainActivity.this);

        rvEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabAddEvent.getVisibility() == View.VISIBLE) {
                    fabAddEvent.hide();
                } else if (dy < 0 && fabAddEvent.getVisibility() != View.VISIBLE) {
                    fabAddEvent.show();
                }
            }
        });

        rvEvents.setAdapter(customEventAdapter);
        rvEvents.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        EditText etSearchBar = findViewById(R.id.etSearchBar);
        etSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBar.setExpanded(false);
                if (fabAddEvent.getVisibility() != View.VISIBLE)
                    fabAddEvent.show();
            }
        });
        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                Query firebaseSearchQuery = databaseEventReference.orderByChild("name").startAt(String.valueOf(s))
                        .endAt(s + "uf8ff");

                firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        events.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            MainActivity.events.add(0, ds.getValue(Event.class));
                        }
                        customEventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                if (fabAddEvent.getVisibility() != View.VISIBLE)
                    fabAddEvent.show();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createEvent(Event event) {
        databaseEventReference.child(String.valueOf(event.id)).setValue(event);
    }

    private void readEvents() {
        databaseEventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    MainActivity.events.add(0, children.getValue(Event.class));
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
                            etEventDescription.getText().toString(),
                            new ArrayList<Participant>()));
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Etkinlik adını girin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
