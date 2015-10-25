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
import com.bmustapha.ultramediaplayer.adapters.AlbumAdapter;
import com.bmustapha.ultramediaplayer.models.Album;
import com.bmustapha.ultramediaplayer.utilities.MediaQuery;

import java.util.ArrayList;

/**
 * Created by tunde on 10/25/15.
 */
public class AlbumFragment extends Fragment {

    private ArrayList<Album> albums;
    private Typeface face;
    private RecyclerView albumRecyclerView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albums = new ArrayList<Album>();
        face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");

        albumRecyclerView = (RecyclerView) view.findViewById(R.id.album_recycler_view);
        albumRecyclerView.setHasFixedSize(true);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        new getAlbumList().execute();

        return view;
    }

    private void getAllAlbums() {
        try {
            albums = MediaQuery.getAlbumList(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class getAlbumList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // get all play lists (if any)
            getAllAlbums();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            AlbumAdapter albumAdapter = new AlbumAdapter(albums, getActivity(), face);
            albumRecyclerView.setAdapter(albumAdapter);
        }
    }
}
