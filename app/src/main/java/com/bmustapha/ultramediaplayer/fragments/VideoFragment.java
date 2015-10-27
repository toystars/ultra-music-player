package com.bmustapha.ultramediaplayer.fragments;

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
import com.bmustapha.ultramediaplayer.adapters.video.VideoAdapter;
import com.bmustapha.ultramediaplayer.models.Video;
import com.bmustapha.ultramediaplayer.utilities.MediaQuery;

import java.util.ArrayList;

/**
 * Created by tunde on 10/27/15.
 */
public class VideoFragment extends Fragment {

    private ArrayList<Video> videos;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Typeface face;
    private VideoAdapter recyclerAdapter;

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("Videos");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        videos = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        new getVideos().execute();

        return view;
    }

    private class getVideos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // set font
            face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
            // get all play lists (if any)
            getVideos();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerAdapter = new VideoAdapter(videos, face);
            recyclerView.setAdapter(recyclerAdapter);
        }
    }

    private void getVideos() {
        try {
            videos = MediaQuery.getAllVideos(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
