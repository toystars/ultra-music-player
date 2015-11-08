package com.bmustapha.ultramediaplayer.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.services.VideoService;


/**
 * Created by andela on 9/30/15.
 */
public class MediaServiceStartBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startMusicService(context);
        startVideoService(context);
    }

    private void startMusicService(Context context) {
        Intent musicServiceIntent = new Intent(context, MusicService.class);
        context.startService(musicServiceIntent);
    }

    private void startVideoService(Context context) {
        Intent videoServiceIntent = new Intent(context, VideoService.class);
        context.startService(videoServiceIntent);
    }
}
