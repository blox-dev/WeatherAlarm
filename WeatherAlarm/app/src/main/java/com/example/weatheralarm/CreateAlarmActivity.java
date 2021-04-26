package com.example.weatheralarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateAlarmActivity extends AppCompatActivity {

    Spinner hourSpinner;
    Spinner minuteSpinner;
    Spinner alarmSoundSpinner;
    Switch TTSSwitch;
    EditText descriptionEditText;
    ArrayList<String> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        hourSpinner = (Spinner) findViewById(R.id.hourSpinner);
        minuteSpinner = (Spinner) findViewById(R.id.minuteSpinner);
        alarmSoundSpinner = (Spinner) findViewById(R.id.alarmSoundSpinner);
        TTSSwitch = (Switch) findViewById(R.id.TTSSwitch);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        ArrayList<String> hourList = new ArrayList<>();
        ArrayList<String> minuteList = new ArrayList<>();
        songsList = new ArrayList<>();

        Calendar rightNow = Calendar.getInstance();

        for(int i=0 ; i<24 ; ++i)
            hourList.add(i<10 ? '0' + String.valueOf(i) : String.valueOf(i));

        for(int i=0 ; i<60 ; ++i)
            minuteList.add(i<10 ? '0' + String.valueOf(i) : String.valueOf(i));

        songsList.add("ppc1");
        songsList.add("ppc2");

        ArrayAdapter<String> hourAdapter= new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, hourList);
        hourSpinner.setAdapter(hourAdapter);

        ArrayAdapter<String> minuteAdapter= new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, minuteList);
        minuteSpinner.setAdapter(minuteAdapter);

//        hourSpinner.setSelection(rightNow.get(Calendar.HOUR_OF_DAY));
//        minuteSpinner.setSelection(rightNow.get(Calendar.MINUTE));

        if(rightNow.get(Calendar.MINUTE) == 59)
        {
            hourSpinner.setSelection(rightNow.get(Calendar.HOUR_OF_DAY) + 1);
            minuteSpinner.setSelection(0);
        }
        else
        {
            hourSpinner.setSelection(rightNow.get(Calendar.HOUR_OF_DAY));
            minuteSpinner.setSelection(rightNow.get(Calendar.MINUTE) + 1);
        }

        ArrayAdapter<String> songAdapter= new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, songsList);
        alarmSoundSpinner.setAdapter(songAdapter);
        alarmSoundSpinner.setSelection(0);

    }

    public void saveAlarm(View view) {
        int hourr = hourSpinner.getSelectedItemPosition();
        int minn = minuteSpinner.getSelectedItemPosition();

        String time = ((hourr < 10) ? '0' + String.valueOf(hourr) : String.valueOf(hourr)) + ":" + ((minn < 10) ? '0' + String.valueOf(minn) : String.valueOf(minn));

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("time", time);
        intent.putExtra("description", descriptionEditText.getText().toString());
        intent.putExtra("song", TTSSwitch.isChecked() ? null : songsList.get(alarmSoundSpinner.getSelectedItemPosition()));
        setResult(1,intent);
        finish();
    }
}