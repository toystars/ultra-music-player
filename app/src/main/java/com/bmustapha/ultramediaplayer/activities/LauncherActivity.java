package com.bmustapha.ultramediaplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.utilities.UtilFunctions;
import com.victor.loading.newton.NewtonCradleLoading;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        NewtonCradleLoading newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        boolean isServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), getApplicationContext());
        if (!isServiceRunning || (MusicService.musicService == null)) {
            Intent musicServiceIntent = new Intent(getApplicationContext(), MusicService.class);
            startService(musicServiceIntent);
            // pause for a while to get the service running before moving to main screen...
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }
            }, 3000);
        } else {
            // service is already running, move at once
            Intent mainMusicScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainMusicScreenIntent);
            finish();
        }
    }
}
