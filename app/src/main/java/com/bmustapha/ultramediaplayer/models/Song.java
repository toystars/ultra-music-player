package com.bmustapha.ultramediaplayer.models;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by toystars on 8/25/15.
 *
 */

public class Song implements Comparable<Song> {

    private long id;
    Uri trackUri;
    private String title;
    private String artist;
    private long duration;
    private String album;
    private Uri albumArt;
    long fullSeconds;
    int playListId;
    private int songDbId;

    public Song() {

    }

    public Song(long id, String artist, String title, long duration, String album, Uri albumArt, Uri trackUri) {
        this.artist = artist;
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.albumArt = albumArt;
        this.trackUri = trackUri;
        this.playListId = 0;
    }

    public Song(long id, String artist, String title, long duration, String album, Uri albumArt, Uri trackUri, int playListId, int songDbId) {
        this.artist = artist;
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.albumArt = albumArt;
        this.trackUri = trackUri;
        this.playListId = playListId;
        this.songDbId = songDbId;
    }

    /*
        define getter methods to return information
        about music class
     */

    public void setId(long id) {
        this.id = id;
    }
    public long getID() {
        return id;
    }

    public void setTrackUri(Uri uri) {
        this.trackUri = uri;
    }
    public Uri getTrackUri() {
        return trackUri;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getArtist() {
        return artist;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    public long getDuration() {
        return duration;
    }

    public void setAlbumArtUri(Uri uri) {
        this.albumArt = uri;
    }
    public Uri getAlbumArtUri() {
        return albumArt;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    public String getAlbum() {
        return album;
    }

    public void setPlayListId(int id) {
        this.playListId = id;
    }
    public int getPlayListId() {
        return this.playListId;
    }

    public void setSongDbId(int songDbId) {
        this.songDbId = songDbId;
    }
    public int getSongDbId() {
        return songDbId;
    }

    // method to get formatted time for song
    public String getFormattedTime() {
        String finalMinutes;
        String finalSeconds;
        fullSeconds = TimeUnit.MILLISECONDS.toSeconds(getDuration());
        // get minutes from supplied seconds
        int minutes = (int) fullSeconds / 60;
        int seconds = (int) fullSeconds % 60;
        finalMinutes = Integer.toString(minutes);
        finalSeconds = Integer.toString(seconds);
        if (minutes < 10) {
            finalMinutes = "0" + minutes;
        }
        if (seconds < 10) {
            finalSeconds = "0" + seconds;
        }
        return finalMinutes + ":" + finalSeconds;
    }

    @Override
    public int compareTo(@NonNull Song song) {
        return this.getTitle().compareTo(song.getTitle());
    }
}
