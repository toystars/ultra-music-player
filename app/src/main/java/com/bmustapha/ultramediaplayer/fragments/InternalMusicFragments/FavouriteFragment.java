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
import com.bmustapha.ultramediaplayer.adapters.FavouritesAdapter;
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
    private PlayListDB playListDB;
    private Typeface face;
    private MusicService musicService;
    private RecyclerView favouritesRecyclerView;
    private GridLayoutManager layoutManager;

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
        playListDB = PlayListSync.getDataBaseHandler();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        favouritesRecyclerView = (RecyclerView) view.findViewById(R.id.fav_recycler_view);
        favouritesRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        favouritesRecyclerView.setLayoutManager(layoutManager);

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
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(favouriteSongs, getActivity(), face);
            favouritesRecyclerView.setAdapter(favouritesAdapter);
            PlayListSync.updateFavouritesAdapter(favouritesAdapter);
        }
    }
}
