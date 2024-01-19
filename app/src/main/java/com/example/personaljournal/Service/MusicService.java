package com.example.personaljournal.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.personaljournal.R;

import java.security.Provider;

public class MusicService extends Service {

    MediaPlayer music;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        music = MediaPlayer.create(this,R.raw.lofi);
        music.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        music.start();

        return  START_STICKY; // Service will be restarted if killed by the system

    }

    @Override
    public void onDestroy() {
       music.stop();
        music.release();
        super.onDestroy();
    }



}
