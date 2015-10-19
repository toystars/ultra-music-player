package com.bmustapha.ultramediaplayer.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bmustapha.ultramediaplayer.services.MusicService;


/**
 * Created by andela on 9/30/15.
 */
public class MusicServiceStartBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent musicServiceIntent = new Intent(context, MusicService.class);
        context.startService(musicServiceIntent);
    }
}
