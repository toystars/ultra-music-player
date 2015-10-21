package com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.PlayListAdapter;
import com.bmustapha.ultramediaplayer.adapters.PlayListAdapter2;
import com.bmustapha.ultramediaplayer.adapters.SongAdapter;
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
 * Created by andela on 10/19/15.
 */
public class PlayListFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private MusicService musicService;
    private ArrayList<PlayList> playLists;
    private ListView playListView;
    private PlayListDB playListDB;
    private PlayListAdapter2 playListAdapter2;
    private Typeface face;
    private int playListPosition;
    private SongAdapter songAdapter;

    RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    PlayListAdapter recyclerAdapter;

    @Override
    public void onStart() {
        super.onStart();
        musicService = MusicService.musicService;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_recycler, container, false);

        playLists = new ArrayList<>();
        playListView = (ListView) view.findViewById(R.id.playlist_listview);
        playListDB = PlayListSync.getDataBaseHandler();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);


//        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                playListPosition = position;
//                getPlayListSongs(position);
//            }
//        });
//
//        playListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                playListPosition = position;
//                showMenu(view);
//                return true;
//            }
//        });

        new getPlayLists().execute();

        return view;
    }

    private class getPlayLists extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // set font
            face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
            // get all play lists (if any)
            getPlayLists();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // create adapter and set to list view
            // playListAdapter2 = new PlayListAdapter2(getActivity(), playLists, face);
            // playListView.setAdapter(playListAdapter2);
            recyclerAdapter = new PlayListAdapter(playLists, getActivity());
            recyclerView.setAdapter(recyclerAdapter);
            PlayListSync.updateAdapter(recyclerAdapter);
        }
    }

    private void getPlayListSongs(int position) {
        // change to activity containing playList songs
//        PlayList playList = playListAdapter2.getItem(position);
//        Intent intent = new Intent(getActivity(), PlayListSongs.class);
//        intent.putExtra("PLAYLIST_ID", playList.getDbId());
//        intent.putExtra("PLAYLIST_NAME", playList.getName());
//        startActivity(intent);
    }

    private void getPlayLists() {
        try {
            playLists = playListDB.getAllPlayLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_songs:
                addSong(playListAdapter2);
                return true;
            case R.id.edit_play_list:
                View view = getActivity().getLayoutInflater().inflate(R.layout.add_playlist, null);
                new PlayListModal(getActivity(), playListAdapter2.getItem(playListPosition), true).showDialog(view);
                return true;
            case R.id.delete_play_list:
                deletePlayList();
                return true;
            default:
                return false;
        }
    }

    private void addSong(PlayListAdapter2 playListAdapter2) {
        ArrayList<Song> songList = SongListHelper.getAllSongs(getActivity());
        Collections.sort(songList);
        songAdapter = new SongAdapter(getActivity(), songList, face, false, false);
        final LayoutInflater inflater = getActivity().getLayoutInflater();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
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
            PlayList playList = playListAdapter2.getItem(playListPosition);
            message = playList.addSong(song);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Unable to add song.";
        } finally {
            PlayListSync.refreshPlayLists();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePlayList() {
        // confirm if user wants to delete playlist
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder
                .setTitle("Delete Playlist")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Deletion is irreversible!")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlayList playList = playListAdapter2.getItem(playListPosition);
                        String message = "";
                        try {
                            playListDB.deletePlayList(playList.getDbId());
                            message = "Playlist deleted.";
                        } catch (Exception e) {
                            e.printStackTrace();
                            message = "Unable to delete playlist";
                        } finally {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            PlayListSync.refreshPlayLists();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
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
}
