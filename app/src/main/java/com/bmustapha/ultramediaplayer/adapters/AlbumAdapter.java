package com.bmustapha.ultramediaplayer.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.Album;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tunde on 10/25/15.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<Album> albums;
    private Activity activity;
    private Typeface face;
    private MusicService musicService;
    private int albumPosition;

    public AlbumAdapter(ArrayList<Album> albums, Activity activity, Typeface face) {
        super();
        this.albums = albums;
        this.activity = activity;
        this.face = face;
        musicService = MusicService.musicService;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder holder, final int position) {
        Album album = albums.get(position);
        holder.albumName.setText(album.getName());
        holder.artistName.setText(album.getArtistName());
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumPosition = position;
                // showMenu(view);
            }
        });
        Picasso.with(activity)
                .load(album.getArt())
                .error(AlbumArtLoader.getDefaultArt())
                .into(holder.albumArt);
        holder.albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // selectSong(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView albumName;
        public TextView artistName;
        public ImageView moreButton;
        public ImageView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.playlist_name);
            albumName.setTypeface(face);
            artistName = (TextView) itemView.findViewById(R.id.playlist_description);
            artistName.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.playlist_more_button);
            albumArt = (ImageView) itemView.findViewById(R.id.playlist_art);
        }
    }
}
