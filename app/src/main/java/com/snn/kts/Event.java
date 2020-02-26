package com.snn.kts;

public class Event {
    public String id;
    public String name;
    public String date;
    public String time;
    public String location;
    public String description;

    Event() {
    }

    Event(String id, String name, String date, String time, String location, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
    }
}
