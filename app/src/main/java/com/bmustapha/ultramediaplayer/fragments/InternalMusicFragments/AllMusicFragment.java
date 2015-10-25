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
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.utilities.MediaQuery;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by andela on 10/19/15.
 */
public class AllMusicFragment extends Fragment {

    private ListView songView;
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

        songView = (ListView) view.findViewById(R.id.song_list);

        songList = new ArrayList<>();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSong(position);
            }
        });

        new getAllSongs().execute();

        return view;
    }

    private void selectSong(int position) {
        musicService.setSongList(songList);
        musicService.startSong(position);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
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
            // Locate the listview in listview_main.xml
            SongAdapter songAdapter = new SongAdapter(getActivity(), songList, face, true, false);
            songView.setAdapter(songAdapter);
        }
    }
}
