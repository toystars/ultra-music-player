package com.bmustapha.ultramediaplayer.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;

public class FullMusicActivity extends AppCompatActivity {

    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_music);

        musicService = MusicService.musicService;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        LinearLayout fullScreenAlbumArt = (LinearLayout) findViewById(R.id.full_screen_album_art);


        Song currentSong = musicService.getCurrentSong();

        Bitmap fullPlayListBitmap = AlbumArtLoader.getTrackCoverArt(this, currentSong.getAlbumArtUri());
        if (fullPlayListBitmap != null) {
            fullScreenAlbumArt.setBackgroundDrawable(new BitmapDrawable(fullPlayListBitmap));
        } else {
            fullScreenAlbumArt.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_art));
        }

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
        finish();
    }
}
