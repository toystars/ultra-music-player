package com.bmustapha.ultramediaplayer.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bmustapha.ultramediaplayer.services.MusicService;


public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case MusicService.NOTIFY_NEXT:
                MusicService.musicService.playNext();
                break;
            case MusicService.NOTIFY_PAUSE_PLAY:
                MusicService.musicService.toggleState();
                break;
            case MusicService.NOTIFY_PREV:
                MusicService.musicService.playPrevious();
                break;
            case MusicService.NOTIFY_CLOSE:
                MusicService.musicService.removeNotification();
                break;
            case MusicService.NOTIFY_FULL_MUSIC:
                MusicService.musicService.openFullMusicScreen();
                break;
        }
    }
}
