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

import java.util.ArrayList;

public class AlarmAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> times;
    ArrayList<String> descriptions;
    ArrayList<String> songs;
    boolean[] actives;
    LayoutInflater inflater;

    public AlarmAdapter(Context applicationContext) {
        this.context = applicationContext;
        inflater = LayoutInflater.from(applicationContext);
    }

    public AlarmAdapter(Context applicationContext, ArrayList<String> times, ArrayList<String> descriptions, ArrayList<String> songs, boolean[] actives) {
        this.context = applicationContext;
        this.times = times;
        this.descriptions = descriptions;
        this.songs = songs;
        this.actives = actives;
        inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return times.size();
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


        timeTextView.setText(times.get(i));
        descriptiontextView.setText(descriptions.get(i));
        activeSwitch.setChecked(actives[i]);

        return convertView;
    }
}