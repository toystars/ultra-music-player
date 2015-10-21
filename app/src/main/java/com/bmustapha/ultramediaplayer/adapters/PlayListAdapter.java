package com.bmustapha.ultramediaplayer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.PlayList;

import java.util.ArrayList;

/**
 * Created by tunde on 10/21/15.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private ArrayList<PlayList> playLists;
    private Context context;

    public PlayListAdapter(ArrayList<PlayList> playLists, Context context) {
        super();
        this.playLists = playLists;
        this.context = context;
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
                Toast.makeText(context, playLists.get(position).getName(), Toast.LENGTH_SHORT).show();
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
        public ImageView playlistArt;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.playlist_name);
            description = (TextView) itemView.findViewById(R.id.playlist_description);
            moreButton = (ImageView) itemView.findViewById(R.id.playlist_more_button);
        }
    }

    public void setPlayLists(ArrayList<PlayList> newPlayLists) {
        playLists = newPlayLists;
    }
}
