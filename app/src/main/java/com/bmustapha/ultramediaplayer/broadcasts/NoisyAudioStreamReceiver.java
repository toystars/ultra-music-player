package com.bmustapha.ultramediaplayer.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.bmustapha.ultramediaplayer.services.MusicService;


/**
 * Created by tunde on 10/1/15.
 */
public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
            if (MusicService.musicService.isPlaying()) {
                MusicService.musicService.toggleState();
            }
        }
    }
}
