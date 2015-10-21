package com.bmustapha.ultramediaplayer.shared;

import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.bmustapha.ultramediaplayer.adapters.PlayListAdapter;
import com.bmustapha.ultramediaplayer.adapters.SongAdapter;
import com.bmustapha.ultramediaplayer.database.PlayListDB;

/**
 * Created by tunde on 9/12/15.
 */
public class PlayListSync {

    public static PlayListAdapter mPlayListAdapter;
    public static PlayListDB mPlayListDB;
    public static SongAdapter mSongAdapter;
    public static RelativeLayout songDetail;

    public static void updateDatabaseHandler(PlayListDB playListDB) {
        mPlayListDB = playListDB;
    }

    public static PlayListDB getDataBaseHandler() {
        return mPlayListDB;
    }

    public static void updateAdapter(PlayListAdapter playListAdapter) {
        mPlayListAdapter = playListAdapter;
    }

    public static RecyclerView.Adapter getPlayListAdapter() {
        return mPlayListAdapter;
    }

    public static void refreshPlayLists() {
        mPlayListAdapter.setPlayLists(mPlayListDB.getAllPlayLists());
        mPlayListAdapter.notifyDataSetChanged();
    }

    public static void refreshFavouritesSongs(SongAdapter songAdapter) {
        songAdapter.setSongs(mPlayListDB.getAllFavSongs());
        songAdapter.notifyDataSetChanged();
    }

    public static void updateSongAdapter(SongAdapter songAdapter) {
        mSongAdapter = songAdapter;
    }

    public static void loadSongs(int position) {
        mSongAdapter.setSongs(mPlayListDB.getAllSongs(position));
    }
}
