package com.example.weatheralarm;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable, Comparable {
    String time;
    String description;
    String song;
    boolean active;
    PendingIntent pendingIntent;

    Alarm()
    {
        time = "00:00";
        description = "Test alarm";
        song = null;
        pendingIntent = null;
    }
    Alarm(String time, String description, String song, boolean active, PendingIntent pendingIntent)
    {
        this.time = time;
        this.description = description;
        this.song = song;
        this.active = active;
        this.pendingIntent = pendingIntent;
    }

    protected Alarm(Parcel in) {
        time = in.readString();
        description = in.readString();
        song = in.readString();
        active = in.readByte() != 0;
        pendingIntent = in.readParcelable(PendingIntent.class.getClassLoader());
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "{" + time + ", " + description + ", " + song + ", " + active + "}";
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(description);
        dest.writeString(song);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeParcelable(pendingIntent, flags);
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Alarm))
            return 0;
        Alarm other = (Alarm) o;

        return this.time.compareTo(other.time);
    }
}
