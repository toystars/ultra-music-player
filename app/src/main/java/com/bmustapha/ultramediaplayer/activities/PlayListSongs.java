package com.bmustapha.ultramediaplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.FullScreenPlayListSongsAdapter;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;

import java.util.ArrayList;

public class PlayListSongs extends AppCompatActivity {

    String playListName;
    int playListId;
    ArrayList<Song> playListSongs;
    MusicService musicService;
    ListView listView;
    int currentPosition;
    private RelativeLayout currentAlbumArt;
    private boolean isRegistered = false;
    private SeekBar fullPlayListSeekBar;
    private ImageView fullPlayListPlayPauseButton;
    private LinearLayout fullPlayListControlLayout;
    private TextView fullPlayListSongName;
    private TextView fullPlayListArtistName;

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
        fullPlayListSeekBar = (SeekBar) findViewById(R.id.full_playlist_seekbar);
        ImageView fullPlayListPreviousButton = (ImageView) findViewById(R.id.full_playlist_previous);
        fullPlayListPlayPauseButton = (ImageView) findViewById(R.id.full_playlist_pause_play);
        ImageView fullPlayListNextButton = (ImageView) findViewById(R.id.full_playlist_next);
        fullPlayListControlLayout = (LinearLayout) findViewById(R.id.full_playlist_control_layout);
        fullPlayListSongName = (TextView) findViewById(R.id.full_playlist_song_name);
        fullPlayListArtistName = (TextView) findViewById(R.id.full_playlist_artist_name);
        final ImageView fullPlayListRepeatButton = (ImageView) findViewById(R.id.full_playlist_repeat);
        final ImageView fullPlayListShuffleButton = (ImageView) findViewById(R.id.full_playlist_shuffle);
        final FloatingActionButton muteButton = (FloatingActionButton) findViewById(R.id.mute_floating_button);

        musicService = MusicService.musicService;
        playListSongs = new ArrayList<>();

        actionBar.setTitle(playListName);
        getAllPlayListSongs(playListId);

        if (musicService.getRepeatState()) {
            fullPlayListRepeatButton.setImageResource(R.drawable.ic_playlist_full_repeat_enabled);
        } else {
            fullPlayListRepeatButton.setImageResource(R.drawable.ic_playlist_full_repeat_disabled);
        }

        if (musicService.getShuffledState()) {
            fullPlayListShuffleButton.setImageResource(R.drawable.ic_playlist_full_shuffle_enabled);
        } else {
            fullPlayListShuffleButton.setImageResource(R.drawable.ic_playlist_full_shuffle_disabled);
        }

        if (musicService.getMuteState()) {
            muteButton.setImageResource(R.drawable.ic_volume_mute);
        } else {
            muteButton.setImageResource(R.drawable.ic_volume_up);
        }

        if (musicService.getPlayListId() == playListId) {
            Song currentSong = musicService.getCurrentSong();
            musicService.setSongListFromPlayList(playListSongs, true, playListId, currentAlbumArt, fullPlayListPlayPauseButton);

            if (musicService.isPlaying()) {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
            } else {
                fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_play_pause);
            }

            Bitmap fullPlayListBitmap = AlbumArtLoader.getTrackCoverArt(this, currentSong.getAlbumArtUri());
            if (fullPlayListBitmap != null) {
                currentAlbumArt.setBackgroundDrawable(new BitmapDrawable(fullPlayListBitmap));
            } else {
                currentAlbumArt.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_art));
            }

            fullPlayListSongName.setText(currentSong.getTitle());
            fullPlayListArtistName.setText(currentSong.getArtist());

            setUpUpdates();
            setUpControls();
        } else {
            musicService.setSongListFromPlayList(true, currentAlbumArt, fullPlayListPlayPauseButton);
            fullPlayListControlLayout.setVisibility(View.GONE);
            fullPlayListSeekBar.setVisibility(View.GONE);
        }

        Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        FullScreenPlayListSongsAdapter fullScreenPlayListSongsAdapter = new FullScreenPlayListSongsAdapter(this, playListSongs, face, playListId);
        listView.setAdapter(fullScreenPlayListSongsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentPosition = position;
                selectSong();
            }
        });

        fullPlayListRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleRepeat();
                if (musicService.getRepeatState()) {
                    fullPlayListRepeatButton.setImageResource(R.drawable.ic_playlist_full_repeat_enabled);
                } else {
                    fullPlayListRepeatButton.setImageResource(R.drawable.ic_playlist_full_repeat_disabled);
                }
            }
        });

        fullPlayListShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleShuffle();
                if (musicService.getShuffledState()) {
                    fullPlayListShuffleButton.setImageResource(R.drawable.ic_playlist_full_shuffle_enabled);
                } else {
                    fullPlayListShuffleButton.setImageResource(R.drawable.ic_playlist_full_shuffle_disabled);
                }
            }
        });

        fullPlayListPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playPrevious();
            }
        });

        fullPlayListPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleState();
                if (musicService.isPlaying()) {
                    fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
                } else {
                    fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_play_pause);
                }
            }
        });

        fullPlayListNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playNext();
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleMute();
                if (musicService.getMuteState()) {
                    muteButton.setImageResource(R.drawable.ic_volume_mute);
                } else {
                    muteButton.setImageResource(R.drawable.ic_volume_up);
                }
            }
        });

        fullPlayListSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekTo(progress);
                    fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setUpControls() {
        fullPlayListSeekBar.setMax(musicService.getDuration());
        fullPlayListSeekBar.setProgress(musicService.getCurrentPosition());
    }

    private void setUpUpdates() {
        musicService.setUpHandler();
        registerReceiver(broadcastReceiver, new IntentFilter(MusicService.BROADCAST_ACTION));
        isRegistered = true;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        String counter = intent.getStringExtra("counter");
        String mediaMax = intent.getStringExtra("mediaMax");
        String formattedTime = intent.getStringExtra("formattedTime");
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");

        int seekProgress = Integer.parseInt(counter);
        int seekMax = Integer.parseInt(mediaMax);


        fullPlayListSeekBar.setMax(seekMax);
        fullPlayListSeekBar.setProgress(seekProgress);

        if (!songName.equals(fullPlayListSongName.getText().toString())) {
            fullPlayListSongName.setText(songName);
        }

        if (!artistName.equals(fullPlayListArtistName.getText().toString())) {
            fullPlayListArtistName.setText(artistName);
        }
    }

    private void selectSong() {
        musicService.setSongListFromPlayList(playListSongs, true, playListId, currentAlbumArt, fullPlayListPlayPauseButton);
        musicService.startSong(currentPosition);
        musicService.getPlayPauseButton().setImageResource(R.drawable.ic_activity_pause);
        fullPlayListPlayPauseButton.setImageResource(R.drawable.ic_playlist_full_pause);
        if (fullPlayListControlLayout.getVisibility() == View.GONE) {
            fullPlayListControlLayout.setVisibility(View.VISIBLE);
            fullPlayListSeekBar.setVisibility(View.VISIBLE);
        }
        if (!isRegistered) {
            setUpUpdates();
        }
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
                if (isRegistered) {
                    try {
                        unregisterReceiver(broadcastReceiver);
                        isRegistered = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                musicService.disableHandler();
                musicService.clearFullPlaylistParams();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                isRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        musicService.disableHandler();
        musicService.clearFullPlaylistParams();
        finish();
    }

    private void getAllPlayListSongs(int playListDbId) {
        playListSongs = PlayListSync.getDataBaseHandler().getAllPlayListSongs(playListDbId);
    }
}
