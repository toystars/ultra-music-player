package com.bmustapha.ultramediaplayer.activities;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bmustapha.ultramediaplayer.R;

public class FullVideoActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer videoMediaPlayer;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFixedSize(800, 480);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoMediaPlayer = new MediaPlayer();

        try {
            videoUri = getIntent().getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoMediaPlayer.setOnPreparedListener(this);
        videoMediaPlayer.setOnCompletionListener(this);
        videoMediaPlayer.setOnErrorListener(this);
        videoMediaPlayer.setOnSeekCompleteListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoMediaPlayer.pause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        videoMediaPlayer.setDisplay(surfaceHolder);
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void playVideo() {
        try {
            videoMediaPlayer.setDataSource(getApplicationContext(), videoUri);
            videoMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        videoMediaPlayer.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
