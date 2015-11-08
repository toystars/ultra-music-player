package com.bmustapha.ultramediaplayer.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.services.VideoService;
import com.bmustapha.ultramediaplayer.shared.CurrentPlayingVideoKeeper;

public class FullVideoActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private Uri videoUri;
    private LinearLayout videoControlLayout;
    private boolean controlVisible = true;
    private SeekBar seekBar;

    private final Handler handler = new Handler();
    private LinearLayout videoDetailsLayout;
    private ImageView playPauseButton;
    private ImageView rotateScreenButton;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);

        setUp();
    }

    private void setUpUpdates() {
        VideoService.videoService.setUpHandler();
        registerReceiver(broadcastReceiver, new IntentFilter(VideoService.BROADCAST_ACTION));
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

        int seekProgress = Integer.parseInt(counter);
        int seekMax = Integer.parseInt(mediaMax);


        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
    }

    private void setUp() {
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        // surfaceHolder.setFixedSize(800, 480);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        videoControlLayout = (LinearLayout) findViewById(R.id.video_control_layout);
        videoDetailsLayout = (LinearLayout) findViewById(R.id.video_detail_layout);
        TextView videoNameTextView = (TextView) findViewById(R.id.video_name);
        playPauseButton = (ImageView) findViewById(R.id.video_play_pause_button);
        seekBar = (SeekBar) findViewById(R.id.video_seek_bar);
        rotateScreenButton = (ImageView) findViewById(R.id.full_screen_video_rotate_button);

        VideoService.videoService.setActivityParams(this, surfaceView, videoNameTextView,
                CurrentPlayingVideoKeeper.getCurrentPlayingVideo(), surfaceHolder);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    VideoService.videoService.getVideoMediaPlayer().seekTo(progress);
                    playPauseButton.setImageResource(R.drawable.ic_video_pause);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoService.videoService.getVideoMediaPlayer().isPlaying()) {
                    VideoService.videoService.getVideoMediaPlayer().pause();
                    playPauseButton.setImageResource(R.drawable.ic_video_play);
                } else {
                    VideoService.videoService.getVideoMediaPlayer().start();
                    playPauseButton.setImageResource(R.drawable.ic_video_pause);
                }
            }
        });

        rotateScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoService.videoService.getVideoMediaPlayer().isPlaying()) {
                    VideoService.videoService.pause();
                }
                final int orientation = getResources().getConfiguration().orientation;
                switch (orientation) {
                    case 1:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case 2:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
            }
        });
    }

    private void toggleView() {
        if (controlVisible) {
            hideViews();
        } else {
            showViews();
        }
    }

    private void hideViews() {
        // Prepare the View for the animation
        videoControlLayout.setVisibility(View.VISIBLE);

        // Start the animation
        videoControlLayout.animate()
                .translationY(videoControlLayout.getHeight());
        videoDetailsLayout.setVisibility(View.GONE);
        controlVisible = false;
    }

    private void showViews() {
        videoControlLayout.animate()
                .translationY(0);
        videoDetailsLayout.setVisibility(View.VISIBLE);
        controlVisible = true;
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        VideoService.videoService.play(CurrentPlayingVideoKeeper.getCurrentPlayingVideo());
        setUpUpdates();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void finishActivity() {
        if (isRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                isRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        VideoService.videoService.disableHandler();
        CurrentPlayingVideoKeeper.resetCurrentPlayingVideo();
        VideoService.videoService.stopVideo();
        finish();
    }

//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {

//    }
//
//    private void playVideo() {
//        VideoService.videoService.playVideo();
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//    }



//    @Override
//    public void onSeekComplete(MediaPlayer mediaPlayer) {
//        mediaPlayer.start();
//    }

//
//    private void startTimer() {
//        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                toggleView();
//            }
//        };
//        countDownTimer.start();
//    }

    // seekBar.setMax(videoMediaPlayer.getDuration());
    // seekBar.setProgress(videoMediaPlayer.getCurrentPosition());

}
