package com.bmustapha.ultramediaplayer.utilities;

/**
 * Created by andela on 9/16/15.
 */
public class TimeFormatter {

    public static String getTimeString(long millis) {

        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000*60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60) ) / 1000;

        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
