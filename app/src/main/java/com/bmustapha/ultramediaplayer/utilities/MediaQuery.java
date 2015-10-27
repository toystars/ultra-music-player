package com.bmustapha.ultramediaplayer.utilities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.bmustapha.ultramediaplayer.models.Album;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.models.Video;

import java.util.ArrayList;

/**
 * Created by tunde on 9/12/15.
 */
public class MediaQuery {

    /*
        Audio Queries
     */
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

    public static ArrayList<Song> getAlbumSongs(Context context, String albumName) {
        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String whereVal[] = {albumName};
        String orderBy = android.provider.MediaStore.Audio.Media.TITLE;
        ArrayList<Song> songList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where, whereVal, orderBy);

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
        if (cursor != null) {
            cursor.close();
        }
        return songList;
    }


    /*
        Video Queries
     */
    public static ArrayList<Video> getAllVideos(Activity activity) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        ArrayList<Video> videoList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                Uri VideoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
                Video video = new Video(id, name, size, thumbnail, VideoUri);
                videoList.add(video);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return videoList;
    }
}


