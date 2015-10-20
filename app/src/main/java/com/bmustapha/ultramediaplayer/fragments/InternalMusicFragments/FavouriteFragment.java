package com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.SongAdapter;
import com.bmustapha.ultramediaplayer.database.PlayListDB;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;

import java.util.ArrayList;

/**
 * Created by andela on 10/19/15.
 */
public class FavouriteFragment extends Fragment {

    private ArrayList<Song> favouriteSongs;
    private ListView favouriteSongListView;
    private PlayListDB playListDB;
    private Typeface face;
    private MusicService musicService;

    @Override
    public void onStart() {
        super.onStart();
        musicService = MusicService.musicService;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        favouriteSongs = new ArrayList<Song>();
        favouriteSongListView = (ListView) view.findViewById(R.id.favourites_song_list);
        playListDB = PlayListSync.getDataBaseHandler();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        favouriteSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSong(position);
            }
        });

        new getFavouriteSongs().execute();

        return view;
    }

    private void getFavSongs() {
        try {
            favouriteSongs = playListDB.getAllFavSongs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectSong(int position) {
        musicService.setSongList(favouriteSongs);
        musicService.startSong(position);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
    }

    private class getFavouriteSongs extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // get all play lists (if any)
            getFavSongs();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            // create adapter and set to list view
            SongAdapter favSongListAdapter =  new SongAdapter(getActivity(), favouriteSongs, face, true, true);
            favouriteSongListView.setAdapter(favSongListAdapter);
        }
    }
}
