package com.example.weatheralarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //check if all is good
            startStuff();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_alarm);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println(location);
                weatherAPI.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=f096b18194f12f6daccbad9e40084475");
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
        startStuff();
    }



    public void startStuff() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 100, mLocationListener);

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
    }

    public void onExitButtonPress(View view) {
        if(!mIsMediaPlayerReleased)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mIsMediaPlayerReleased = true;
        }
        mLocationManager.removeUpdates(mLocationListener);
        finish();
    }
}