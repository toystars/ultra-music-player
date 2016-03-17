package com.bmustapha.ultramediaplayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.MusicPagerAdapter;
import com.bmustapha.ultramediaplayer.database.PlayListDB;
import com.bmustapha.ultramediaplayer.modals.PlayListModal;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MusicService musicService = MusicService.musicService;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shuffle = sharedPreferences.getBoolean("playbackShuffle", false);
        boolean repeat = sharedPreferences.getBoolean("playbackRepeat", false);
        musicService.toggleShuffle(shuffle);
        musicService.toggleRepeat(repeat);

        PlayListDB playListDB = new PlayListDB(this);
        PlayListSync.updateDatabaseHandler(playListDB);
        AlbumArtLoader.initializeDefaultArt();

        LinearLayout controlLayout = (LinearLayout) findViewById(R.id.main_controls);
        ImageView playPauseButton = (ImageView) findViewById(R.id.play_pause_button);
        SimpleDraweeView albumArt = (SimpleDraweeView) findViewById(R.id.album_art);
        TextView trackName = (TextView) findViewById(R.id.track_name);
        TextView artistName = (TextView) findViewById(R.id.track_artist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        MusicPagerAdapter adapter = new MusicPagerAdapter(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        // check if music service contains songs is playing to determine if the control layout should be displayed or not
        if (musicService.getCurrentSong() != null) {
            controlLayout.setVisibility(View.VISIBLE);
        } else {
            controlLayout.setVisibility(View.GONE);
        }

        musicService.setParams(controlLayout, playPauseButton, albumArt, trackName, artistName);

        if (musicService.getCurrentSong() != null) {
            Song song = musicService.getCurrentSong();
            // a song is either currently playing or has been selected, update the track details
            trackName.setText(song.getTitle());
            artistName.setText(song.getArtist());
            if (musicService.isPlaying()) {
                playPauseButton.setImageResource(R.drawable.ic_activity_pause);
            } else {
                playPauseButton.setImageResource(R.drawable.ic_activity_play);
            }
            AlbumArtLoader.setImage(song.getAlbumArtUri(), albumArt);
            controlLayout.setEnabled(true);
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleState();
            }
        });

        controlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullMusicActivityIntent = new Intent(MainActivity.this, FullMusicActivity.class);
                startActivity(fullMusicActivityIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.playlist_add:
                View view = getLayoutInflater().inflate(R.layout.add_playlist, null);
                new PlayListModal(this).showDialog(view);
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, UserSettingsActivity.class);
                startActivity(settingsIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
