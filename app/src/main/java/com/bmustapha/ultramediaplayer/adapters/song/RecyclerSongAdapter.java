package com.bmustapha.ultramediaplayer.adapters.song;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.playlist.PlayListAddSongAdapter;
import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.bmustapha.ultramediaplayer.utilities.ContextProvider;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by toystars on 12/30/15.
 */
public class RecyclerSongAdapter extends RecyclerView.Adapter<RecyclerSongAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Song> songs;
    private Typeface face;
    private Context context;
    private Activity activity;
    private int songPosition;
    private Song currentSong;
    private MusicService musicService;

    public RecyclerSongAdapter(Context context, ArrayList<Song> theSongs, Typeface face, MusicService musicService) {
        super();
        this.context = context;
        this.activity = (Activity) context;
        this.songs = theSongs;
        this.face = face;
        this.musicService = musicService;
    }

    @Override
    public RecyclerSongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerSongAdapter.ViewHolder holder, final int position) {
        final Song song = songs.get(position);
        //get title and artist strings
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.songDuration.setText(song.getFormattedTime());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContextProvider.getContext());
        boolean displaySongAlbumArt = sharedPreferences.getBoolean("allSongsAlbumArt", true);
        if (displaySongAlbumArt) {
            holder.albumArt.setVisibility(View.VISIBLE);
            AlbumArtLoader.setImage(song.getAlbumArtUri(), holder.albumArt);
        } else {
            holder.albumArt.setVisibility(View.GONE);
        }

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songPosition = position;
                showMenu(view);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSong(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView songTitle;
        public TextView songArtist;
        public TextView songDuration;
        public SimpleDraweeView albumArt;
        public ImageView moreButton;

        public ViewHolder(View itemView) {
            super(itemView);
            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songTitle.setTypeface(face);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songArtist.setTypeface(face);
            songDuration = (TextView) itemView.findViewById(R.id.song_duration);
            songDuration.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.more_button);
            albumArt = (SimpleDraweeView) itemView.findViewById(R.id.song_album_art);
        }
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_playlist:
                addToPlayList();
                return true;
            case R.id.add_to_favourite:
                addToFavourites();
                return true;
            case R.id.set_ringtone:
                setRingTone();
                return true;
            default:
                return false;
        }
    }

    public void showMenu(View v) {
        currentSong = songs.get(songPosition);
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_songlist_pop);
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

    public void setSongs(ArrayList<Song> newSongList) {
        songs = newSongList;
    }

    private void addToPlayList() {
        // get a list of playLists available on the device
        final ArrayList<PlayList> playLists = PlayListSync.getDataBaseHandler().getAllPlayLists();
        // get the view
        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.playlist_fragment, null);

        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setMessage("Add song to Playlist")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        ListView listView = (ListView) view.findViewById(R.id.play_list_view);
        PlayListAddSongAdapter playListAdapter2 = new PlayListAddSongAdapter(activity, playLists, face);
        listView.setAdapter(playListAdapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // add song to playList if not in playlist before...
                String message = "";
                try {
                    PlayList playList = playLists.get(position);
                    message = playList.addSong(currentSong);
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Unable to add song.";
                } finally {
                    alertDialog.dismiss();
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRingTone() {
        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Set as ringtone?")
                .setIcon(R.drawable.ic_phone_android)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, currentSong.getTrackUri());
                            Toast.makeText(context, "Ringtone set successfully.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error setting ringtone!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addToFavourites() {
        String message = "";
        try {
            if (PlayListSync.getDataBaseHandler().isInFavourites(currentSong.getID())) {
                message = "Song already in Favourites";
            } else {
                long songId = currentSong.getID();
                String artist = currentSong.getArtist();
                String title = currentSong.getTitle();
                long duration = currentSong.getDuration();
                String album = currentSong.getAlbum();
                String albumURI = currentSong.getAlbumArtUri().toString();
                String trackURI = currentSong.getTrackUri().toString();
                PlayListSync.getDataBaseHandler().addToFavourites(songId, artist, title, duration, album, albumURI, trackURI);
                message = "Song added to Favourites";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error adding song to Favourites";
        } finally {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            // PlayListSync.refreshFavouritesSongs();
        }

    }

    private void selectSong(int position) {
        musicService.setSongList(songs);
        musicService.startSong(position);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
    }
}
