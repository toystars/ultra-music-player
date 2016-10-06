package com.bmustapha.ultramediaplayer.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.models.Song;

import java.util.ArrayList;

/**
 * Created by toystars on 9/13/15.
 *
 */

public class PlayListDB extends SQLiteOpenHelper {

    // define database name
    public static final String DATABASE_NAME = "UltraPlayList.db";
    public static final int DATABASE_VERSION = 1;

    // define tables name
    public static final String PLAYLIST_TABLE_NAME = "playlist";
    public static final String SONG_TABLE_NAME = "song";
    public static final String FAVOURITES_TABLE_NAME = "favourites";

    // define columns for playlist
    public static final String PLAYLIST_COLUMN_ID = "id";
    public static final String PLAYLIST_COLUMN_NAME = "name";
    public static final String PLAYLIST_COLUMN_DESCRIPTION = "description";

    // define columns for song
    public static final String SONG_COLUMN_ID = "id";
    public static final String SONG_COLUMN_TRACK_ID = "trackId";
    public static final String SONG_COLUMN_ARTIST = "artist";
    public static final String SONG_COLUMN_TITLE = "title";
    public static final String SONG_COLUMN_DURATION = "duration";
    public static final String SONG_COLUMN_ALBUM = "album";
    public static final String SONG_COLUMN_ALBUM_ART_URI = "albumArtURI";
    public static final String SONG_COLUMN_TRACK_URI = "trackURI";
    public static final String SONG_COLUMN_PLAYLIST_ID = "playListId";

    // define columns for song
    public static final String FAV_COLUMN_ID = "id";
    public static final String FAV_COLUMN_TRACK_ID = "trackId";
    public static final String FAV_COLUMN_ARTIST = "artist";
    public static final String FAV_COLUMN_TITLE = "title";
    public static final String FAV_COLUMN_DURATION = "duration";
    public static final String FAV_COLUMN_ALBUM = "album";
    public static final String FAV_COLUMN_ALBUM_ART_URI = "albumArtURI";
    public static final String FAV_COLUMN_TRACK_URI = "trackURI";


    public PlayListDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PLAYLIST_TABLE_NAME + " (" +
                        "id integer primary key," +
                        "name text," +
                        "description text)"
        );
        db.execSQL("create table " + SONG_TABLE_NAME + " (" +
                        "id integer primary key," +
                        "trackId int," +
                        "artist text," +
                        "title text," +
                        "duration int," +
                        "album text," +
                        "albumArtURI text," +
                        "trackURI text," +
                        "playListId int)"
        );
        db.execSQL("create table " + FAVOURITES_TABLE_NAME + " (" +
                        "id integer primary key," +
                        "trackId int," +
                        "artist text," +
                        "title text," +
                        "duration int," +
                        "album text," +
                        "albumArtURI text," +
                        "trackURI text," +
                        "playListId int)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FAVOURITES_TABLE_NAME);
        onCreate(db);
    }

    // method to write new playlist to database
    public boolean insertPlayList(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        db.insert("playlist", null, contentValues);
        return true;
    }

    // method to write update playlist in database
    public boolean updatePlayList(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("description", description);
        db.update("playlist", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    // method to delete playlist
    public Integer deletePlayList(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // find all songs linked to the playlist
        ArrayList<Song> songs = getAllPlayListSongs(id);
        // loop through the songs ArrayList to delete each song
        if (songs.size() > 0) {
            for (int x = 0; x < songs.size(); x++) {
                deleteSong(songs.get(x).getSongDbId());
            }
        }
        return db.delete(PLAYLIST_TABLE_NAME, "id = ? ", new String[] {Integer.toString(id)});
    }

    // get single PlayList
    public PlayList getPlayList(int dbId) {
        int id = 0;
        String name = "";
        String description = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from playlist where id=" + dbId, null);
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                id = res.getInt(res.getColumnIndex(PLAYLIST_COLUMN_ID));
                name = res.getString(res.getColumnIndex(PLAYLIST_COLUMN_NAME));
                description = res.getString(res.getColumnIndex(PLAYLIST_COLUMN_DESCRIPTION));
                res.moveToNext();
            }
            res.close();
        }
        return new PlayList(name, description, id);
    }

    public ArrayList<PlayList> getAllPlayLists() {
        ArrayList<PlayList> allPlayLists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from playlist", null);
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                PlayList playList = new PlayList();
                playList.setName(res.getString(res.getColumnIndex(PLAYLIST_COLUMN_NAME)));
                playList.setDescription(res.getString(res.getColumnIndex(PLAYLIST_COLUMN_DESCRIPTION)));
                playList.setDbId(res.getInt(res.getColumnIndex(PLAYLIST_COLUMN_ID)));
                allPlayLists.add(playList);
                res.moveToNext();
            }
            res.close();
        }
        return allPlayLists;
    }


    // method to write new song to database
    public boolean insertSong(
            long trackId, String artist, String title, long duration, String album, String albumURI, String trackURI, int playListId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("trackId", trackId);
        contentValues.put("artist", artist);
        contentValues.put("title", title);
        contentValues.put("duration", duration);
        contentValues.put("album", album);
        contentValues.put("albumArtURI", albumURI);
        contentValues.put("trackURI", trackURI);
        contentValues.put("playListId", playListId);
        db.insert("song", null, contentValues);
        return true;
    }

    // method to update song in database
    public boolean updateSong(
            int id, long trackId, String artist, String title, long duration, String album, String albumURI, String trackURI, int playListId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("trackId", trackId);
        contentValues.put("artist", artist);
        contentValues.put("title", title);
        contentValues.put("duration", duration);
        contentValues.put("album", album);
        contentValues.put("albumArtURI", albumURI);
        contentValues.put("trackURI", trackURI);
        contentValues.put("playListId", playListId);
        db.update("song", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    // method to delete song
    public Integer deleteSong(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("song", "id = ? ", new String[] {Integer.toString(id)});
    }

    public boolean isInPlayList(int id, long songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from song where playListId=" + id, null );
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                if (songId == res.getLong(res.getColumnIndex(SONG_COLUMN_TRACK_ID))) {
                    return true;
                }
                res.moveToNext();
            }
        }
        res.close();
        return false;
    }

    public Uri getFirstTrackUri(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from song where playListId=" + id, null );
        if (res.moveToFirst()) {
            return Uri.parse(res.getString(res.getColumnIndex(SONG_COLUMN_ALBUM_ART_URI)));
        }
        res.close();
        return null;
    }

    public ArrayList<Song> getAllPlayListSongs(int playListId) {
        ArrayList<Song> allSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from song where playListId=" + playListId, null );
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                Song song = new Song();
                song.setSongDbId(res.getInt(res.getColumnIndex(SONG_COLUMN_ID)));
                song.setId(res.getLong(res.getColumnIndex(SONG_COLUMN_TRACK_ID)));
                song.setArtist(res.getString(res.getColumnIndex(SONG_COLUMN_ARTIST)));
                song.setTitle(res.getString(res.getColumnIndex(SONG_COLUMN_TITLE)));
                song.setDuration(res.getLong(res.getColumnIndex(SONG_COLUMN_DURATION)));
                song.setAlbum(res.getString(res.getColumnIndex(SONG_COLUMN_ALBUM)));
                song.setAlbumArtUri(Uri.parse(res.getString(res.getColumnIndex(SONG_COLUMN_ALBUM_ART_URI))));
                song.setTrackUri(Uri.parse(res.getString(res.getColumnIndex(SONG_COLUMN_TRACK_URI))));
                song.setPlayListId(res.getInt(res.getColumnIndex(SONG_COLUMN_PLAYLIST_ID)));
                allSongs.add(song);
                res.moveToNext();
            }
        }
        res.close();
        return allSongs;
    }

    public boolean isInFavourites(long songId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FAVOURITES_TABLE_NAME + " where trackId=" + songId, null );
        if (res.getCount() > 0) {
            return true;
        }
        res.close();
        return false;
    }

    // method to write new song to database
    public boolean addToFavourites(
            long trackId, String artist, String title, long duration, String album, String albumURI, String trackURI) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("trackId", trackId);
        contentValues.put("artist", artist);
        contentValues.put("title", title);
        contentValues.put("duration", duration);
        contentValues.put("album", album);
        contentValues.put("albumArtURI", albumURI);
        contentValues.put("trackURI", trackURI);
        db.insert(FAVOURITES_TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<Song> getAllFavSongs() {
        ArrayList<Song> allSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FAVOURITES_TABLE_NAME, null );
        if (res.moveToFirst()) {
            while (!res.isAfterLast()) {
                Song song = new Song();
                song.setSongDbId(res.getInt(res.getColumnIndex(FAV_COLUMN_ID)));
                song.setId(res.getLong(res.getColumnIndex(FAV_COLUMN_TRACK_ID)));
                song.setArtist(res.getString(res.getColumnIndex(FAV_COLUMN_ARTIST)));
                song.setTitle(res.getString(res.getColumnIndex(FAV_COLUMN_TITLE)));
                song.setDuration(res.getLong(res.getColumnIndex(FAV_COLUMN_DURATION)));
                song.setAlbum(res.getString(res.getColumnIndex(FAV_COLUMN_ALBUM)));
                song.setAlbumArtUri(Uri.parse(res.getString(res.getColumnIndex(FAV_COLUMN_ALBUM_ART_URI))));
                song.setTrackUri(Uri.parse(res.getString(res.getColumnIndex(FAV_COLUMN_TRACK_URI))));
                allSongs.add(song);
                res.moveToNext();
            }
        }
        res.close();
        return allSongs;
    }

    // method to delete song
    public Integer deleteFavSong(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FAVOURITES_TABLE_NAME, "trackId = ? ", new String[] {Long.toString(id)});
    }
}

