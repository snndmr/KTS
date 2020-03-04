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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Event> events = new ArrayList<>();

    private Toast toast;
    private FloatingActionButton fabAddEvent;
    private EventAdapter eventAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private DatabaseReference databaseEventReference;

    /*void createTestValues() {
        String[] events = getResources().getStringArray(R.array.events);
        String[] students = getResources().getStringArray(R.array.students);
        String[] participants = getResources().getStringArray(R.array.participants);


        for (String event : events) {
            ArrayList<Participant> participantsList = new ArrayList<>();

            for (int j = 0; j < ThreadLocalRandom.current().nextInt(100, 500); j++) {
                String[] temp = students[j].split(",");
                participantsList.add(new Student(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5]));
            }

            for (int j = 0; j < ThreadLocalRandom.current().nextInt(100, 500); j++) {
                String[] temp = participants[j].split(",");
                participantsList.add(new Participant(temp[0], temp[1], temp[2], temp[3]));
            }

            String[] temp = event.split(",");
            createEvent(new Event(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], participantsList));
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        readEvents();
        /* createTestValues(); */
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
        eventAdapter = new EventAdapter(MainActivity.this);

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

        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        EditText etSearchBar = findViewById(R.id.etSearchBar);
        etSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBar.setExpanded(false);
            }
        });
        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                eventAdapter.getFilter().filter(s);

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
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    events.add(0, children.getValue(Event.class));
                }
                eventAdapter.notifyDataSetChanged();

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
                    mToast.showToast(MainActivity.this, "Etkinlik adını girin!");
                }
            }
        });
        dialog.show();
    }
}
