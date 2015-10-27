package com.bmustapha.ultramediaplayer.adapters.video;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.activities.FullVideoActivity;
import com.bmustapha.ultramediaplayer.models.Video;
import com.bmustapha.ultramediaplayer.utilities.ContextProvider;

import java.util.ArrayList;

/**
 * Created by tunde on 10/27/15.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    ArrayList<Video> videos;
    Typeface face;

    int videoPosition;

    public VideoAdapter(ArrayList<Video> videos, Typeface face) {
        this.videos = videos;
        this.face = face;
    }

    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.ViewHolder holder, final int position) {
        final Video video = videos.get(position);
        holder.name.setText(video.getName());
        holder.size.setText(String.valueOf(video.getSize()));
        holder.thumbnail.setImageBitmap(video.getThumbnail());

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoIntent = new Intent(ContextProvider.getContext(), FullVideoActivity.class);
                videoIntent.setData(videos.get(position).getUri());
                videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextProvider.getContext().startActivity(videoIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView size;
        public ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.video_thumbnail);
            name = (TextView) itemView.findViewById(R.id.video_name);
            name.setTypeface(face);
            size = (TextView) itemView.findViewById(R.id.video_size);
            size.setTypeface(face);
        }
    }
}
