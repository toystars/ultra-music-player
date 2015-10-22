package com.bmustapha.ultramediaplayer.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.FullScreenPlayListSongsAdapter;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;

import java.util.ArrayList;

public class PlayListSongs extends AppCompatActivity {

    String playListName;
    int playListId;
    ArrayList<Song> playListSongs;
    MusicService musicService;
    ListView listView;
    int currentPosition;
    private RelativeLayout currentAlbumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_songs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        playListName = getIntent().getStringExtra("PLAYLIST_NAME");
        playListId = getIntent().getIntExtra("PLAYLIST_ID", 0);

        currentAlbumArt = (RelativeLayout) findViewById(R.id.current_album_art);
        listView = (ListView) findViewById(R.id.full_playlist_song_list_view);
        musicService = MusicService.musicService;
        playListSongs = new ArrayList<>();

        actionBar.setTitle(playListName);
        getAllPlayListSongs(playListId);

        Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        FullScreenPlayListSongsAdapter fullScreenPlayListSongsAdapter = new FullScreenPlayListSongsAdapter(this, playListSongs, face);
        listView.setAdapter(fullScreenPlayListSongsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentPosition = position;
                selectSong();
            }
        });
    }

    private void selectSong() {
        musicService.setSongListFromPlayList(playListSongs, true, playListId, currentAlbumArt);
        musicService.startSong(currentPosition);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playlist_song_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                musicService.clearFullPlaylistParams();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        musicService.clearFullPlaylistParams();
        finish();
    }

    private void getAllPlayListSongs(int playListDbId) {
        playListSongs = PlayListSync.getDataBaseHandler().getAllPlayListSongs(playListDbId);
    }
}
