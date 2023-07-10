package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class SplashActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        surfaceView = findViewById(R.id.surfaceView);
        mediaPlayer = MediaPlayer.create(this,R.raw.spalash_screen);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();


        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

mediaPlayer.setDisplay(surfaceHolder);

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });


        mediaPlayer.start();
        Intent i = new Intent(this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(i);
                finish();
                mediaPlayer.stop();

            }
        },4000);



    }
}