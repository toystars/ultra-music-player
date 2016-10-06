package com.bmustapha.ultramediaplayer.shared;

import com.bmustapha.ultramediaplayer.models.Video;

/**
 * Created by toystars on 11/8/15.
 *
 */

public class CurrentPlayingVideoKeeper {

    public static Video currentPlayingVideo;

    public static void setCurrentPlayingVideo(Video video) {
        currentPlayingVideo = video;
    }

    public static Video getCurrentPlayingVideo() {
        return currentPlayingVideo;
    }

    public static void resetCurrentPlayingVideo() {
        currentPlayingVideo = null;
    }
}
