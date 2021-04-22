package com.example.weatheralarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    AlarmManager manager;

    ListView alarmList;

    ArrayList<String> times = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> songs = new ArrayList<>();
    boolean[] actives = new boolean[100];

    private boolean firstOpen = true;
    private int alarmNr;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("restore");
//        alarmNr = savedInstanceState.getInt("alarmNr");
//        times = savedInstanceState.getStringArrayList("times");
//        songs = savedInstanceState.getStringArrayList("songs");
//        descriptions = savedInstanceState.getStringArrayList("descriptions");
//        actives = savedInstanceState.getBooleanArray("actives");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("save");
//        outState.putInt("alarmNr", alarmNr);
//        outState.putStringArrayList("times", times);
//        outState.putStringArrayList("songs", songs);
//        outState.putStringArrayList("descriptions", descriptions);
//        outState.putBooleanArray("actives", actives);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null)
            return;

        Bundle extras = data.getExtras();

        if(extras == null)
            return;

        if (extras.containsKey("time")) {
            times.add(extras.getString("time"));
//            System.out.println(times);
//            System.out.println(times.size());
        }
        if (extras.containsKey("description")) {
            descriptions.add(extras.getString("description"));
//            System.out.println(descriptions);
//            System.out.println(descriptions.size());
        }
        if(extras.containsKey("song")) {
            songs.add(extras.getString("song"));
//            System.out.println(songs);
//            System.out.println(songs.size());
        }
        actives[alarmNr] = true;

        alarmList = (ListView) findViewById(R.id.simpleListView);
        AlarmAdapter alarmAdapter = new AlarmAdapter(getApplicationContext(), times, descriptions, songs, actives);
        alarmList.setAdapter(alarmAdapter);

        // actually set the alarm
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        String time = times.get(alarmNr);

//        System.out.println(time);
//        System.out.println(Integer.parseInt(time.substring(0,2)));
//        System.out.println(Integer.parseInt(time.substring(3,5)));

        cal_alarm.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time.substring(0,2)));
        cal_alarm.set(Calendar.MINUTE,Integer.parseInt(time.substring(3,5)));
        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        System.out.println("Yep clock: " + (cal_alarm.getTimeInMillis() - cal_now.getTimeInMillis()) / 1000);
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        manager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);

        alarmNr++;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(firstOpen){
            alarmNr = 3;
            firstOpen = false;
            times.add("08:00");
            times.add("12:12");
            times.add("23:15");
            descriptions.add("Dentist");
            descriptions.add("Smechereala");
            descriptions.add("Somn");
            songs.add("ppc1");
            songs.add(null);
            songs.add("ppc2");
            actives[0] = true;
            actives[1] = true;
            actives[2] = false;
        }
        alarmList = (ListView) findViewById(R.id.simpleListView);
        AlarmAdapter alarmAdapter = new AlarmAdapter(getApplicationContext(), times, descriptions, songs, actives);
        alarmList.setAdapter(alarmAdapter);
    }

    public void addAlarm(View view) {
        if(alarmNr > 99)
            Toast.makeText(this, "Too many alarms lol", Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(this, CreateAlarmActivity.class);
            startActivityForResult(intent, 1);
        }
    }
}