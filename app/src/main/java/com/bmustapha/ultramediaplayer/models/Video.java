package com.bmustapha.ultramediaplayer.models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by andela on 10/27/15.
 */
public class Video {

    private long id;
    private String name;
    private long size;
    private Uri uri;
    private Bitmap thumbnail;

    public Video(long id, String name, long size, Bitmap thumbnail, Uri uri) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.thumbnail = thumbnail;
        this.uri = uri;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
