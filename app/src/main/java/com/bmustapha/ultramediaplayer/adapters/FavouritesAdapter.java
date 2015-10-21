package com.bmustapha.ultramediaplayer.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.database.PlayListDB;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by tunde on 10/21/15.
 */
public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private final PlayListDB playListDB;
    private ArrayList<Song> songs;
    private Activity context;
    private Typeface face;

    private SongAdapter songAdapter;
    private int songPosition;

    private MusicService musicService;

    private ArrayList<Song> playListSongs;

    public FavouritesAdapter(ArrayList<Song> songs, Activity context, Typeface face) {
        super();
        this.songs = songs;
        this.context = context;
        this.face = face;
        playListDB = PlayListSync.getDataBaseHandler();
        musicService = MusicService.musicService;
    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, final int position) {
        Song song = songs.get(position);
        holder.songName.setText(song.getTitle());
        holder.artistName.setText(song.getArtist());
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songPosition = position;
                showMenu(view);
            }
        });
        Picasso.with(context)
                .load(song.getAlbumArtUri())
                .error(AlbumArtLoader.getDefaultArt())
                .into(holder.favouriteAlbumArt);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songName;
        public TextView artistName;
        public ImageView moreButton;
        public ImageView favouriteAlbumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.playlist_name);
            songName.setTypeface(face);
            artistName = (TextView) itemView.findViewById(R.id.playlist_description);
            artistName.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.playlist_more_button);
            favouriteAlbumArt = (ImageView) itemView.findViewById(R.id.playlist_art);
        }
    }

    public void setPlayLists(ArrayList<Song> newSongs) {
        songs = newSongs;
    }

    public ArrayList<Song> getPlayLists() {
        return songs;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_fav_song_list_pop);
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            Log.w("TAG", "error forcing menu icons to show", e);
            popup.show();
            return;
        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_fav_song:
                removeSongFromFavourites();
            default:
                return false;
        }
    }

    private void removeSongFromFavourites() {
        String message = "";
        try {
            PlayListSync.getDataBaseHandler().deleteFavSong(songs.get(songPosition).getID());
            message = "Song removed from Favourites";
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error removing song from Favourites";
        } finally {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            PlayListSync.refreshFavouritesSongs();
        }
    }

    public void setSongs(ArrayList<Song> newSongs) {
        songs = newSongs;
    }
}