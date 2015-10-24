package com.bmustapha.ultramediaplayer.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.bmustapha.ultramediaplayer.activities.PlayListSongs;
import com.bmustapha.ultramediaplayer.database.PlayListDB;
import com.bmustapha.ultramediaplayer.modals.PlayListModal;
import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.SongListHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tunde on 10/21/15.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private final PlayListDB playListDB;
    private ArrayList<PlayList> playLists;
    private Activity context;
    private Typeface face;

    private SongAdapter songAdapter;
    private int playListPosition;

    private MusicService musicService;

    private ArrayList<Song> playListSongs;

    public PlayListAdapter(ArrayList<PlayList> playLists, Activity context, Typeface face) {
        super();
        this.playLists = playLists;
        this.context = context;
        this.face = face;
        playListDB = PlayListSync.getDataBaseHandler();
        musicService = MusicService.musicService;
    }

    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.ViewHolder holder, final int position) {
        final PlayList playList = playLists.get(position);
        holder.name.setText(playList.getName());
        holder.description.setText(playList.getDescription());
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playListPosition = position;
                showMenu(view);
            }
        });
        holder.playListArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playListPosition = position;
                openPlayList();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView description;
        public ImageView moreButton;
        public ImageView playListArt;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.playlist_name);
            name.setTypeface(face);
            description = (TextView) itemView.findViewById(R.id.playlist_description);
            description.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.playlist_more_button);
            playListArt = (ImageView) itemView.findViewById(R.id.playlist_art);
        }
    }

    private void openPlayList() {
        PlayList playList = playLists.get(playListPosition);
        Intent intent = new Intent(context, PlayListSongs.class);
        intent.putExtra("PLAYLIST_ID", playList.getDbId());
        intent.putExtra("PLAYLIST_NAME", playList.getName());
        context.startActivity(intent);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.playlist_popup_menu);
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
            e.printStackTrace();
            popup.show();
            return;
        }
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_playlist_songs:
                playPlayListSongs();
                return true;
            case R.id.add_songs:
                addSong();
                return true;
            case R.id.edit_play_list:
                View view = context.getLayoutInflater().inflate(R.layout.add_playlist, null);
                new PlayListModal(context, playLists.get(playListPosition), true).showDialog(view);
                return true;
            case R.id.delete_play_list:
                deletePlayList();
                return true;
            default:
                return false;
        }
    }

    private void playPlayListSongs() {
        playListSongs = playListDB.getAllPlayListSongs(playLists.get(playListPosition).getDbId());
        if (playListSongs.size() > 0) {
            musicService.setSongList(playListSongs, playLists.get(playListPosition).getDbId());
            musicService.startSong(0);
        }
    }

    private void deletePlayList() {
        // confirm if user wants to delete playlist
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder
                .setTitle("Delete Playlist")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Deletion is irreversible!")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlayList playList = playLists.get(playListPosition);
                        String message = "";
                        try {
                            playListDB.deletePlayList(playList.getDbId());
                            message = "Playlist deleted.";
                        } catch (Exception e) {
                            e.printStackTrace();
                            message = "Unable to delete playlist";
                        } finally {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            PlayListSync.refreshPlayLists();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addSong() {
        ArrayList<Song> songList = SongListHelper.getAllSongs(context);
        Collections.sort(songList);
        songAdapter = new SongAdapter(context, songList, face, false, false);
        final LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.playlist_add_song, null);

        ListView songView = (ListView) view.findViewById(R.id.song_list);
        songView.setAdapter(songAdapter);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSong(position);
            }
        });
        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Add song to Playlist")
                .setView(view)
                .setIcon(android.R.drawable.ic_input_add)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addSong(int position) {
        // get song
        Song song = songAdapter.getItem(position);
        String message = "";
        try {
            PlayList playList = playLists.get(playListPosition);
            message = playList.addSong(song);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Unable to add song.";
        } finally {
            PlayListSync.refreshPlayLists();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void setPlayLists(ArrayList<PlayList> newPlayLists) {
        playLists = newPlayLists;
    }

    public ArrayList<PlayList> getPlayLists() {
        return playLists;
    }
}
