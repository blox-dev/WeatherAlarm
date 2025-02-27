package com.example.weatheralarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.*;

public class MainActivity extends Activity{

    AlarmManager manager;

    ListView alarmListView;
    
    ArrayList<Alarm> alarmList = new ArrayList<>();
    AlarmAdapter alarmAdapter;

    SharedPreferences sharedPreferences;

    public void saveAlarmList(){
        JSONArray listJson = new JSONArray();

        Alarm a;
        try {
            for(int i=0 ; i<alarmList.size() ; ++i) {
                a = alarmList.get(i);
                JSONObject alarmJson = new JSONObject();
                alarmJson.put("description", a.description);
                alarmJson.put("active", a.active);
                alarmJson.put("intent", a.intent.toUri(0));
                alarmJson.put("song", a.song);
                alarmJson.put("time", a.time);
                listJson.put(i, alarmJson);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        sharedPreferences.edit().putString("alarmList", listJson.toString()).commit();
    }

    public void loadAlarmList(){
        if(sharedPreferences == null)
            sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);

        if (!sharedPreferences.contains("alarmList"))
            return;

        String alarmString = sharedPreferences.getString("alarmList", null);
        if(alarmString == null)
            return;

        alarmList.clear();

        try {
            JSONArray alarmJson = new JSONArray(alarmString);
            for (int i = 0; i < alarmJson.length(); ++i) {
                JSONObject a = alarmJson.getJSONObject(i);

                String description = a.getString("description");
                boolean active = a.getBoolean("active");
                Intent intent = Intent.parseUri(a.getString("intent"), 0);
                String song = a.getString("song");
                String time = a.getString("time");

                alarmList.add(new Alarm(time, description, song, active, intent));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarmList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveAlarmList();
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
            Toast.makeText(this,"Something went wrong.", Toast.LENGTH_LONG).show();
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

        Intent myIntent = new Intent(this, TriggerAlarmActivity.class);

        myIntent.putExtra("time", time);
        myIntent.putExtra("description", description);
        myIntent.putExtra("song", song);

        Alarm alarm = new Alarm(time, description, song, true, myIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, alarm.intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);

        alarmList.add(alarm);

        long secondsUntil = (cal_alarm.getTimeInMillis() - cal_now.getTimeInMillis())/1000;
        long minutesUntil = (secondsUntil / 60) % 60;
        long hoursUntil = (secondsUntil / 3600) % 24;
        long daysUntil = (secondsUntil / (24 *3600));

        System.out.println("Yep clock: " + secondsUntil);

        StringBuilder toastMsg = new StringBuilder("Alarm will go off in ");

        toastMsg.append((daysUntil == 0) ? "": daysUntil + " days, ");
        toastMsg.append((hoursUntil == 0) ? "": hoursUntil + " hours, ");
        toastMsg.append((minutesUntil == 0) ? secondsUntil + " seconds." : minutesUntil + " minutes.");

        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

        prepareAlarmView();

        saveAlarmList();
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

    void prepareAlarmView(){
        Collections.sort(alarmList);
        alarmListView =  findViewById(R.id.simpleListView);
        alarmAdapter = new AlarmAdapter(this, alarmList);
        alarmListView.setAdapter(alarmAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);

        main();
    }

    private void main(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        prepareAlarmView();
    }

    public void addAlarm(View view) {
        if(alarmList.size() > 99)
            Toast.makeText(this, "You have too many alarms set.", Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(this, CreateAlarmActivity.class);
            startActivityForResult(intent, 1);
        }
    }
}