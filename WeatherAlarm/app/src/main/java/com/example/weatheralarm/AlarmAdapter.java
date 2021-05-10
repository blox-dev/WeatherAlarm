package com.example.weatheralarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmAdapter extends BaseAdapter {
    Context context;
    ArrayList<Alarm> alarmList;
    LayoutInflater inflater;

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

        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView descriptiontextView = convertView.findViewById(R.id.descriptionTextView);
        Switch activeSwitch = convertView.findViewById(R.id.activeSwitch);

        timeTextView.setText(alarmList.get(i).time);
        descriptiontextView.setText(alarmList.get(i).description);
        activeSwitch.setChecked(alarmList.get(i).active);

        activeSwitch.setOnClickListener(v -> switchActive(alarmList.get(i), activeSwitch.isChecked()));

        convertView.setClickable(true);
        convertView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            builder.setTitle("Delete alarm");
            builder.setMessage("Are you sure you want to delete \"" + alarmList.get(i).description +"\"?");
            builder.setPositiveButton("Yes",
                    (dialog, which) -> {
                        if(context instanceof MainActivity)
                        {
                            if(activeSwitch.isChecked())
                                switchActive(alarmList.get(i), false);

                            ((MainActivity)context).alarmList.remove(alarmList.get(i));
                            ((MainActivity)context).alarmAdapter.notifyDataSetChanged();
                            ((MainActivity)context).saveAlarmList();
                        }
                    });
            builder.setNegativeButton("No",
                    (dialog, which) -> {});
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
        return convertView;
    }

    public void switchActive(Alarm alarm, boolean isChecked)
    {
        AlarmManager manager;
        if(context instanceof MainActivity) {
            manager = ((MainActivity)context).manager;
        }
        else {
            System.out.println("Something went wrong.");
            return;
        }

        try {
            // intent must be EXACTLY the same in order to be cancelled
            alarm.intent.setAction(null);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarm.intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.cancel(pendingIntent);

            if(!isChecked) {
                int index = ((MainActivity) context).alarmList.indexOf(alarm);
                ((MainActivity) context).alarmList.get(index).active = false;
            }

            System.out.println("Alarm deactivated");

            if (isChecked) {
                Calendar cal_alarm = Calendar.getInstance();
                Calendar cal_now = Calendar.getInstance();

                cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.time.substring(0, 2)));
                cal_alarm.set(Calendar.MINUTE, Integer.parseInt(alarm.time.substring(3, 5)));
                cal_alarm.set(Calendar.SECOND, 0);
                if (cal_alarm.before(cal_now)) {
                    cal_alarm.add(Calendar.DATE, 1);
                }

                manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);

                int index = ((MainActivity) context).alarmList.indexOf(alarm);
                ((MainActivity) context).alarmList.get(index).active = true;
                System.out.println(" and then activated again.");
            }
            ((MainActivity)context).saveAlarmList();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}