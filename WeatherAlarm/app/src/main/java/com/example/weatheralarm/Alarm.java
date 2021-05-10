package com.example.weatheralarm;

import android.content.Intent;

public class Alarm implements Comparable {
    String time;
    String description;
    String song;
    String weatherDescription;
    boolean active;
    Intent intent;

    Alarm(String time, String description, String song, boolean active, Intent intent)
    {
        this.time = time;
        this.description = description;
        this.weatherDescription = null;
        this.song = song;
        this.active = active;
        this.intent = intent;
    }

    @Override
    public String toString() {
        return "{" + time + ", " + description + ", " + song + ", " + active + "}";
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Alarm))
            return 0;
        Alarm other = (Alarm) o;

        return this.time.compareTo(other.time);
    }
}
