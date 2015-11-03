package com.bmustapha.ultramediaplayer.activities;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bmustapha.ultramediaplayer.R;

public class FullVideoActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer videoMediaPlayer;
    private Uri videoUri;
    private LinearLayout videoControlLayout;
    private boolean controlVisible = true;
    private CountDownTimer countDownTimer;
    private SeekBar seekBar;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);

        setUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // videoMediaPlayer.pause();
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
        new Thread(new Runnable() {
            public void run() {
                try {
                    videoMediaPlayer.setDataSource(getApplicationContext(), videoUri);
                    videoMediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

        int videoWidth = videoMediaPlayer.getVideoWidth();
        int videoHeight = videoMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        surfaceView.setLayoutParams(lp);

        surfaceView.setClickable(true);
        videoMediaPlayer.start();
        setUpHandler();
        startTimer();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        videoMediaPlayer.release();
        disableHandler();
        finish();
    }

    private void setUp() {
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
        surfaceHolder = surfaceView.getHolder();
        // surfaceHolder.setFixedSize(800, 480);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoMediaPlayer = new MediaPlayer();

        videoControlLayout = (LinearLayout) findViewById(R.id.video_control_layout);
        seekBar = (SeekBar) findViewById(R.id.video_seek_bar);

        try {
            videoUri = getIntent().getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoMediaPlayer.setOnPreparedListener(this);
        videoMediaPlayer.setOnCompletionListener(this);
        videoMediaPlayer.setOnErrorListener(this);
        videoMediaPlayer.setOnSeekCompleteListener(this);

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
                    videoMediaPlayer.seekTo(progress);
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

    private void toggleView() {
        if (controlVisible) {
            hideViews();
        } else {
            showView();
        }
    }

    private void hideViews() {
        // Prepare the View for the animation
        videoControlLayout.setVisibility(View.VISIBLE);

        // Start the animation
        videoControlLayout.animate()
                .translationY(videoControlLayout.getHeight());
        controlVisible = false;
    }

    private void showView() {
        videoControlLayout.animate()
                .translationY(0);
        controlVisible = true;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                toggleView();
            }
        };
        countDownTimer.start();
    }

    public void setUpHandler() {
        handler.removeCallbacks(updateSeekBar);
        handler.postDelayed(updateSeekBar, 1000);
    }

    public void disableHandler() {
        handler.removeCallbacks(updateSeekBar);
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            updateUI();
            handler.postDelayed(this, 1000);
        }
    };

    private void updateUI() {
        seekBar.setMax(videoMediaPlayer.getDuration());
        seekBar.setProgress(videoMediaPlayer.getCurrentPosition());
    }
}
