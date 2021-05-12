package com.example.weatheralarm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TriggerAlarmActivity extends AppCompatActivity implements AsyncResponse {

    AsyncHTTPRequest weatherAPI = new AsyncHTTPRequest(this);
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    MediaPlayer mediaPlayer;
    boolean mIsMediaPlayerReleased = true;
    String time, description, songName;
    TextToSpeech tts;
    TextView weatherDescription;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                        (dialog, which) -> startStuff());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                startStuff();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_alarm);

        startStuff();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startStuff() {
        weatherDescription = findViewById(R.id.weatherDescription);
        TextView alarmTime = findViewById(R.id.alarmTime);
        TextView alarmDescription = findViewById(R.id.alarmDescription);

        System.out.println("Alarm received");

        Intent intent = getIntent();

        time = intent.getStringExtra("time");
        description = intent.getStringExtra("description");
        songName = intent.getStringExtra("song");

        alarmTime.setText(time);
        alarmDescription.setText(description);

        if (!songName.equals("TTS")) {
            int songFileId = getResources().getIdentifier(songName, "raw", getPackageName());

            if (songFileId == 0)
                return;

            mediaPlayer = MediaPlayer.create(getApplicationContext(), songFileId);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mIsMediaPlayerReleased = false;
        }


        if(!isNetworkAvailable()) {
            System.out.println("network unavailable.");
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest networkChange = new NetworkRequest.Builder().build();
            cm.registerNetworkCallback(networkChange, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    System.out.println("Network is good");
                    search();
                }
            });
        }
        else
            search();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    void search()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println(location);
                try {
                    weatherAPI.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=f096b18194f12f6daccbad9e40084475");
                }
                catch(IllegalStateException e){
                    System.out.println("The task has already been executed.");
                }
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 100, mLocationListener);
    }

    public void speak(String toSpeak)
    {
        tts.setLanguage(Locale.US);
        tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void processFinish(String output) {
        if (songName.equals("TTS")) {
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    speak(output);
                }
            });
        }
        weatherDescription.setText(output);
    }

    public void onMuteButtonPress(View view) {
        if(!mIsMediaPlayerReleased) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mIsMediaPlayerReleased = true;
        }
        if(tts != null)
            tts.stop();
    }

    public void onExitButtonPress(View view) {
        if(!mIsMediaPlayerReleased)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mIsMediaPlayerReleased = true;
        }
        if(mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);
        if(tts != null)
            tts.stop();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onExitButtonPress(null);
    }
}