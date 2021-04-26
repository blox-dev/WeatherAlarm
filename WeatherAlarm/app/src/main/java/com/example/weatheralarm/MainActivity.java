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
import java.util.Collections;
import java.util.Date;

public class MainActivity extends Activity {

    static AlarmManager manager;

    ListView alarmListView;

    ArrayList<Alarm> alarmList = new ArrayList<>();

    private boolean firstOpen = true;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("restore");

        if(savedInstanceState.containsKey("alarmList"))
            alarmList = savedInstanceState.getParcelableArrayList("alarmList");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("save");

        outState.putParcelableArrayList("alarmList", alarmList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null)
            return;

        Bundle extras = data.getExtras();

        if(extras == null)
            return;

        String time = null, description = null, song = null;
        if (extras.containsKey("time")) {
            time = extras.getString("time");
        }
        if (extras.containsKey("description")) {
            description = extras.getString("description");
        }
        if(extras.containsKey("song")) {
            song = extras.getString("song");
        }

        if(time == null || description == null || song == null)
        {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show();
            return;
        }

        // actually set the alarm
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_alarm.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time.substring(0,2)));
        cal_alarm.set(Calendar.MINUTE,Integer.parseInt(time.substring(3,5)));
        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

//        System.out.println(time);
        System.out.println("Yep clock: " + (cal_alarm.getTimeInMillis() - cal_now.getTimeInMillis()) / 1000);

        Intent myIntent = new Intent(this, TriggerAlarmActivity.class);

        myIntent.putExtra("time", time);
        myIntent.putExtra("description", description);
        myIntent.putExtra("song", song);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_ONE_SHOT);

        manager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);

        Alarm alarm = new Alarm(time, description, song, true, pendingIntent);

        alarmList.add(alarm);
        Collections.sort(alarmList);

        alarmListView = (ListView) findViewById(R.id.simpleListView);
        AlarmAdapter alarmAdapter = new AlarmAdapter(getApplicationContext(), alarmList);
        alarmListView.setAdapter(alarmAdapter);
    }

    static public void switchActive(Alarm alarm, boolean isChecked)
    {
        try {
            if (isChecked) {
                Calendar cal_alarm = Calendar.getInstance();
                Calendar cal_now = Calendar.getInstance();

                cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.time.substring(0, 2)));
                cal_alarm.set(Calendar.MINUTE, Integer.parseInt(alarm.time.substring(3, 5)));
                cal_alarm.set(Calendar.SECOND, 0);
                if (cal_alarm.before(cal_now)) {
                    cal_alarm.add(Calendar.DATE, 1);
                }

                manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), alarm.pendingIntent);
                System.out.println("Alarm activated");
            } else {
                System.out.println("Alarm deactivated");
                manager.cancel(alarm.pendingIntent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(firstOpen){
            firstOpen = false;
            alarmList.add(new Alarm("08:00","Dentist","ppc1",true,null));
            alarmList.add(new Alarm("12:12","Smechereala",null,true,null));
            alarmList.add(new Alarm("23:15","Somn","ppc2",false,null));
        }
        Collections.sort(alarmList);
        alarmListView = (ListView) findViewById(R.id.simpleListView);
        AlarmAdapter alarmAdapter = new AlarmAdapter(getApplicationContext(), alarmList);
        alarmListView.setAdapter(alarmAdapter);
    }

    public void addAlarm(View view) {
        if(alarmList.size() > 99)
            Toast.makeText(this, "Too many alarms lol", Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(this, CreateAlarmActivity.class);
            startActivityForResult(intent, 1);
        }
    }
}