package com.bmustapha.ultramediaplayer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.PlayList;

import java.util.ArrayList;

/**
 * Created by tunde on 9/11/15.
 */
public class PlayListAdapter2 extends BaseAdapter {


    private LayoutInflater playlistInflater;
    private ArrayList<PlayList> playLists;
    private Typeface face;

    public PlayListAdapter2(Context context, ArrayList<PlayList> playLists, Typeface face) {
        this.playLists = playLists;
        this.playlistInflater = LayoutInflater.from(context);
        this.face = face;
    }

    @Override
    public int getCount() {
        return playLists.size();
    }

    @Override
    public PlayList getItem(int position) {
        return playLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        TextView playListName;
        TextView playListDescription;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = playlistInflater.inflate(R.layout.playlist, parent, false);
            holder = new ViewHolder();
            holder.playListName = (TextView) convertView.findViewById(R.id.play_list_name);
            holder.playListName.setTypeface(face);
            holder.playListDescription = (TextView) convertView.findViewById(R.id.play_list_description);
            holder.playListDescription.setTypeface(face);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get song using position
        PlayList playList = playLists.get(position);
        //get title and artist strings
        holder.playListName.setText(playList.getName());
        holder.playListDescription.setText(playList.getDescription());

        return convertView;
    }

    public void setPlayLists(ArrayList<PlayList> newPlayLists) {
        playLists = newPlayLists;
    }
}
