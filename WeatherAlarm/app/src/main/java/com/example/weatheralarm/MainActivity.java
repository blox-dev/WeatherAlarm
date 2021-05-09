package com.example.weatheralarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends Activity{

    AlarmManager manager;

    ListView alarmListView;

    ArrayList<Alarm> alarmList = new ArrayList<>();
    AlarmAdapter alarmAdapter;


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

        for(Alarm a: alarmList)
            if(a.time.equals(time)) {
                Toast.makeText(this, "You already have an alarm set at that time.", Toast.LENGTH_LONG).show();
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

        alarmListView = findViewById(R.id.simpleListView);
        alarmAdapter = new AlarmAdapter(this, alarmList);
        alarmListView.setAdapter(alarmAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean ok = true;
            for(int result: grantResults)
                if (result == PackageManager.PERMISSION_DENIED) {
                    ok = false;
                    break;
                }

            if(!ok)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Warning");
                builder.setMessage("This application will not function properly without location access.");
                builder.setPositiveButton("Ok",
                        (dialog, which) -> main());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                main();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main();
    }

    private void main(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Collections.sort(alarmList);
        alarmListView =  findViewById(R.id.simpleListView);
        alarmAdapter = new AlarmAdapter(getApplicationContext(), alarmList);
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