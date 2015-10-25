package com.bmustapha.ultramediaplayer.utilities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.bmustapha.ultramediaplayer.models.Album;
import com.bmustapha.ultramediaplayer.models.Song;

import java.util.ArrayList;

/**
 * Created by tunde on 9/12/15.
 */
public class MediaQuery {

    public static ArrayList<Song> getAllSongs(Activity activity) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        ArrayList<Song> songList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);
                //add songs to list
                songList.add(new Song(id, artist, title, duration, album, albumArtUri, trackUri));
            } while (cursor.moveToNext());
        }
        // close the cursor
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songList;
    }


    public static ArrayList<Album> getAlbumList(Context context) {
        ArrayList<Album> albums = new ArrayList<>();
        ArrayList<String> albumNames = new ArrayList<>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        final Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM_ID,}, selection, null,
                "LOWER (" + MediaStore.Audio.Media.ALBUM + ") ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);
                if (!albumNames.contains(albumName)) {
                    Album album = new Album(albumArtUri, albumName, artist);
                    albums.add(album);
                    albumNames.add(albumName);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return albums;
    }
}


