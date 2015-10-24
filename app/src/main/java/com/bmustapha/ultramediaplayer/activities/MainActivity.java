package com.bmustapha.ultramediaplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.bmustapha.ultramediaplayer.database.PlayListDB;
import com.bmustapha.ultramediaplayer.fragments.MusicFragment;
import com.bmustapha.ultramediaplayer.modals.PlayListModal;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MusicService musicService = MusicService.musicService;
        PlayListDB playListDB = new PlayListDB(this);
        PlayListSync.updateDatabaseHandler(playListDB);
        AlbumArtLoader.setDefaultArt(getResources().getDrawable(R.drawable.default_art));

        LinearLayout controlLayout = (LinearLayout) findViewById(R.id.main_controls);
        ImageView playPauseButton = (ImageView) findViewById(R.id.play_pause_button);
        ImageView albumArt = (ImageView) findViewById(R.id.album_art);
        TextView trackName = (TextView) findViewById(R.id.track_name);
        TextView artistName = (TextView) findViewById(R.id.track_artist);

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
            Picasso.with(this)
                    .load(song.getAlbumArtUri())
                    .error(AlbumArtLoader.getDefaultArt())
                    .into(albumArt);
            controlLayout.setEnabled(true);
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleState();
            }
        });

        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullMusicActivityIntent = new Intent(MainActivity.this, FullMusicActivity.class);
                startActivity(fullMusicActivityIntent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                displayView((String) menuItem.getTitle());
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView("Music");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.playlist_add:
                View view = getLayoutInflater().inflate(R.layout.add_playlist, null);
                new PlayListModal(this).showDialog(view);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayView(String title) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (title) {
            case "Music":
                fragment = new MusicFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Videos":
                // fragment = new FavouritesFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Images":
                // fragment = new PlayListFragment();
                break;
            case "Deezer":
                // fragment = new VideoFragment();
                // PlayListSync.updateAdapter(null);
                break;
            case "Youtube":
                // fragment = new VideoFragment();
                // PlayListSync.updateAdapter(null);
                break;
            default:
                break;
        }

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            // close drawer
            mDrawerLayout.closeDrawers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
