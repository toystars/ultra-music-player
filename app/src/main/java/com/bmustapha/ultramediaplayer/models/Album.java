package com.bmustapha.ultramediaplayer.models;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by toystars on 10/25/15.
 *
 */

public class Album implements Comparable<Album> {

    private String name;
    private Uri art;
    private String artistName;

    public Album(Uri art, String name, String artistName) {
        this.art = art;
        this.name = name;
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Uri getArt() {
        return art;
    }

    public void setArt(Uri art) {
        this.art = art;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Album album) {
        return this.getName().compareTo(album.getName());
    }
}
