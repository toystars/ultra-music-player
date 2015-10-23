package com.bmustapha.ultramediaplayer.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;

import java.util.ArrayList;

/**
 * Created by andela on 10/22/15.
 */
public class FullScreenPlayListSongsAdapter extends BaseAdapter {

    private ArrayList<Song> playListSongs;
    private int songPosition;
    private Typeface face;
    private Activity activity;
    private LayoutInflater playListInflater;

    public FullScreenPlayListSongsAdapter(Activity activity, ArrayList<Song> playListSongs, Typeface face) {
        this.face = face;
        this.playListSongs = playListSongs;
        this.activity = activity;
        this.playListInflater = LayoutInflater.from(activity);
    }

    public class ViewHolder {
        ImageView albumArt;
        TextView songTitle;
        TextView songArtist;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = playListInflater.inflate(R.layout.full_screen_playlist_song, viewGroup, false);
            holder = new ViewHolder();
            holder.songTitle = (TextView) view.findViewById(R.id.song_title);
            holder.songTitle.setTypeface(face);
            holder.songArtist = (TextView) view.findViewById(R.id.song_artist);
            holder.songArtist.setTypeface(face);
            holder.albumArt = (ImageView) view.findViewById(R.id.song_album_art);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //get song using position
        Song song = playListSongs.get(position);
        //get title and artist strings
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        Bitmap fullPlayListBitmap = AlbumArtLoader.getTrackCoverArt(activity, song.getAlbumArtUri());
        if (fullPlayListBitmap != null) {
            holder.albumArt.setBackgroundDrawable(new BitmapDrawable(fullPlayListBitmap));
        } else {
            holder.albumArt.setBackgroundDrawable(AlbumArtLoader.getDefaultArt());
        }

        return view;
    }

    @Override
    public int getCount() {
        return playListSongs.size();
    }

    @Override
    public Song getItem(int index) {
        return playListSongs.get(index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
