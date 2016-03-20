package com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.song.RecyclerSongAdapter;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.MediaQuery;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by andela on 10/19/15.
 */
public class AllMusicFragment extends Fragment {

    private RecyclerView songRecyclerView;
    private ArrayList<Song> songList;
    private Typeface face;
    private MusicService musicService;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        musicService = MusicService.musicService;
        songList = new ArrayList<>();
        songRecyclerView = (RecyclerView) view.findViewById(R.id.song_recycler_view);
        songRecyclerView.setHasFixedSize(true);
        songRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        new getAllSongs().execute();

        return view;
    }

    // helper method to help get song info
    private void getSongList() {
        songList = MediaQuery.getAllSongs(getActivity());
    }

    private class getAllSongs extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // get all songs
            getSongList();
            // sort the songs alphabetically
            Collections.sort(songList);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            RecyclerSongAdapter recyclerSongAdapter = new RecyclerSongAdapter(getActivity(), songList, face, musicService);
            songRecyclerView.setAdapter(recyclerSongAdapter);
            PlayListSync.updateSongAdapter(recyclerSongAdapter);
        }
    }
}
