package com.example.weatheralarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.material.theme.MaterialComponentsViewInflater;

import java.util.ArrayList;

public class AlarmAdapter extends BaseAdapter {
    Context context;
    ArrayList<Alarm> alarmList;
    LayoutInflater inflater;

    public AlarmAdapter(Context applicationContext) {
        this.context = applicationContext;
        inflater = LayoutInflater.from(applicationContext);
    }

    public AlarmAdapter(Context applicationContext, ArrayList<Alarm> alarmList) {
        this.context = applicationContext;
        this.alarmList = alarmList;
        inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if(convertView == null)
            convertView = inflater.inflate(R.layout.activity_listview, null);

        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        TextView descriptiontextView = (TextView) convertView.findViewById(R.id.descriptionTextView);
        Switch activeSwitch = (Switch) convertView.findViewById(R.id.activeSwitch);

        timeTextView.setText(alarmList.get(i).time);
        descriptiontextView.setText(alarmList.get(i).description);
        activeSwitch.setChecked(alarmList.get(i).active);

        activeSwitch.setOnClickListener(v -> MainActivity.switchActive(alarmList.get(i), activeSwitch.isChecked()));

        return convertView;
    }
}