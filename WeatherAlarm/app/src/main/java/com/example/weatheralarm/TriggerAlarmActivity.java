package com.example.weatheralarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import org.w3c.dom.Text;

import java.util.Locale;

public class TriggerAlarmActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String time, description, songName;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_alarm);

        System.out.println("Alarm received");
        Intent intent = getIntent();

        time = intent.getStringExtra("time");
        description = intent.getStringExtra("description");
        songName = intent.getStringExtra("song");

        if (songName.equals("TTS")) {
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        speakk();
                    }
                }
            });
            } else {
            int songFileId = getResources().getIdentifier(songName, "raw", getPackageName());

            if (songFileId == 0)
                return;

            mediaPlayer = MediaPlayer.create(getApplicationContext(), songFileId);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void speakk()
    {
        tts.setLanguage(Locale.US);
        tts.speak("Weather is nice yes", TextToSpeech.QUEUE_ADD, null);
    }


    public void onCancelAlarmButtonPress(View view) {
        if(mediaPlayer != null)
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        finish();
    }

}