package com.bmustapha.ultramediaplayer.models;

import android.net.Uri;

import com.bmustapha.ultramediaplayer.shared.PlayListSync;

/**
 * Created by tunde on 9/11/15.
 */
public class PlayList {

    private int dbId;
    private String name;
    private String description;

    public PlayList() {

    }

    // constructor
    public PlayList(String name, String description, int dbId) {
        this.name = name;
        this.description = description;
        this.dbId = dbId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDbId(int id) {
        this.dbId = id;
    }

    public int getDbId() {
        return dbId;
    }

    public Uri getFirstTrackUri() {
        Uri songUri = PlayListSync.getDataBaseHandler().getFirstTrackUri(this.getDbId());
        return songUri != null ? songUri : null;
    }

    public String addSong(Song song) {
        String message;
        // check if song has already been added to database
        if (PlayListSync.getDataBaseHandler().isInPlayList(this.getDbId(), song.getID())) {
            // song present in database
            message = "Song already in playlist";
        } else {
            // get string and int values of song attributes
            long songId = song.getID();
            String artist = song.getArtist();
            String title = song.getTitle();
            long duration = song.getDuration();
            String album = song.getAlbum();
            String albumURI = song.getAlbumArtUri().toString();
            String trackURI = song.getTrackUri().toString();
            int playListId = this.getDbId();
            try {
                PlayListSync.getDataBaseHandler().insertSong(songId, artist, title, duration, album, albumURI, trackURI, playListId);
                message = "Song added to playlist.";
            } catch (Exception e) {
                e.printStackTrace();
                message = "Unable to add song to playlist";
            }
        }
        return message;
    }

}
