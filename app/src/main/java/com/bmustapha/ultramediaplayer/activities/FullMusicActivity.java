package com.bmustapha.ultramediaplayer.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.bmustapha.ultramediaplayer.utilities.TimeFormatter;
import com.facebook.drawee.view.SimpleDraweeView;

public class FullMusicActivity extends AppCompatActivity {

    private MusicService musicService;
    private boolean isRegistered = false;
    private SeekBar fullScreenSeekBar;
    private TextView fullScreenSongTitle;
    private TextView fullScreenArtistName;
    private TextView fullScreenCurrentTime;
    private TextView fullScreenTotalTime;
    private SimpleDraweeView fullScreenAlbumArt;
    private FloatingActionButton fullScreenPausePlay;
    private ImageView fullScreenRepeatButton;
    private ImageView fullScreenPreviousButton;
    private ImageView fullScreenNextButton;
    private ImageView fullScreenShuffleButton;
    private TextView fullScreenAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_music);

        musicService = MusicService.musicService;
        Song currentSong = musicService.getCurrentSong();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Typeface face = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        fullScreenAlbumArt = (SimpleDraweeView) findViewById(R.id.full_screen_album_art);
        fullScreenSongTitle = (TextView) findViewById(R.id.full_music_screen_song_title);
        fullScreenArtistName = (TextView) findViewById(R.id.full_music_screen_artist_name);
        fullScreenCurrentTime = (TextView) findViewById(R.id.full_music_screen_current_time);
        fullScreenTotalTime = (TextView) findViewById(R.id.full_music_screen_total_time);
        fullScreenSeekBar = (SeekBar) findViewById(R.id.full_screen_seek_bar);
        fullScreenPausePlay = (FloatingActionButton) findViewById(R.id.full_music_screen_play_pause_button);
        fullScreenRepeatButton = (ImageView) findViewById(R.id.full_music_screen_repeat_button);
        fullScreenPreviousButton = (ImageView) findViewById(R.id.full_music_screen_previous_button);
        fullScreenNextButton = (ImageView) findViewById(R.id.full_music_screen_next_button);
        fullScreenShuffleButton = (ImageView) findViewById(R.id.full_music_screen_shuffle_button);
        fullScreenAlbumName = (TextView) findViewById(R.id.full_music_screen_album);

        fullScreenSeekBar.setPadding(0,0,0,0);

//        fullScreenSongTitle.setTypeface(face);
//        fullScreenArtistName.setTypeface(face);
//        fullScreenAlbumName.setTypeface(face);

        String albumName = currentSong.getAlbum();
        fullScreenAlbumName.setText(albumName);
        fullScreenSongTitle.setText(currentSong.getTitle());
        fullScreenArtistName.setText(currentSong.getArtist());
        fullScreenCurrentTime.setText(TimeFormatter.getTimeString(musicService.getCurrentPosition()));
        fullScreenTotalTime.setText(TimeFormatter.getTimeString(musicService.getDuration()));
        fullScreenSeekBar.setMax(musicService.getDuration());
        fullScreenSeekBar.setProgress(musicService.getCurrentPosition());

        if (musicService.isPlaying()) {
            fullScreenPausePlay.setImageResource(R.drawable.ic_pause);
        }

        if (!musicService.getRepeatState()) {
            fullScreenRepeatButton.setImageResource(R.drawable.ic_full_music_repeat_disabled);
        }

        if (!musicService.getShuffledState()) {
            fullScreenShuffleButton.setImageResource(R.drawable.ic_full_music_shuffle_disabled);
        }

        AlbumArtLoader.setImage(currentSong.getAlbumArtUri(), fullScreenAlbumArt);

        setUpUpdates();
        setUpButtonClicks();
    }

    private void setUpButtonClicks() {
        fullScreenRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleRepeat();
                if (musicService.getRepeatState()) {
                    fullScreenRepeatButton.setImageResource(R.drawable.ic_full_music_repeat_enabled);
                } else {
                    fullScreenRepeatButton.setImageResource(R.drawable.ic_full_music_repeat_disabled);
                }
            }
        });

        fullScreenShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleShuffle();
                if (musicService.getShuffledState()) {
                    fullScreenShuffleButton.setImageResource(R.drawable.ic_full_music_shuffle_enabled);
                } else {
                    fullScreenShuffleButton.setImageResource(R.drawable.ic_full_music_shuffle_disabled);
                }
            }
        });

        fullScreenPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playPrevious();
            }
        });

        fullScreenNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.playNext();
            }
        });

        fullScreenPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicService.toggleState();
                if (musicService.isPlaying()) {
                    fullScreenPausePlay.setImageResource(R.drawable.ic_pause);
                } else {
                    fullScreenPausePlay.setImageResource(R.drawable.ic_play_arrow);
                }
            }
        });

        fullScreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekTo(progress);
                    fullScreenCurrentTime.setText(TimeFormatter.getTimeString(musicService.getCurrentPosition()));
                    fullScreenPausePlay.setImageResource(R.drawable.ic_pause);
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
        musicService.clearFullScreenParams();
        finish();
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
                musicService.clearFullScreenParams();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void setUpUpdates() {
        musicService.setUpHandler();
        registerReceiver(broadcastReceiver, new IntentFilter(MusicService.BROADCAST_ACTION));
        musicService.setFullScreenParams(fullScreenAlbumArt, fullScreenPausePlay);
        isRegistered = true;
    }

    private void updateUI(Intent intent) {
        String counter = intent.getStringExtra("counter");
        String mediaMax = intent.getStringExtra("mediaMax");
        String formattedTime = intent.getStringExtra("formattedTime");
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");
        String albumName = (intent.getStringExtra("albumName"));

        int seekProgress = Integer.parseInt(counter);
        int seekMax = Integer.parseInt(mediaMax);


        fullScreenSeekBar.setMax(seekMax);
        fullScreenSeekBar.setProgress(seekProgress);

        fullScreenCurrentTime.setText(formattedTime);
        fullScreenTotalTime.setText(TimeFormatter.getTimeString(seekMax));

        if (!songName.equals(fullScreenSongTitle.getText().toString())) {
            fullScreenSongTitle.setText(songName);
        }

        if (!artistName.equals(fullScreenArtistName.getText().toString())) {
            fullScreenArtistName.setText(artistName);
        }

        if (!fullScreenAlbumName.getText().toString().contains(albumName)) {
            fullScreenAlbumName.setText(albumName);
        }
    }
}
