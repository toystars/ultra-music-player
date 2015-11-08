package com.bmustapha.ultramediaplayer.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.bmustapha.ultramediaplayer.models.Video;
import com.bmustapha.ultramediaplayer.utilities.TimeFormatter;

/**
 * Created by toystars on 11/8/15.
 */
public class VideoService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    public static VideoService videoService;
    private MediaPlayer videoMediaPlayer;
    private Activity fullScreenVideoActivity;
    private SurfaceView videoSurfaceView;
    private TextView videoNameTextView;
    private Video currentVideo;
    private Uri currentVideoUri = null;
    private SurfaceHolder surfaceHolder;

    public static final String BROADCAST_ACTION = "com.bmustapha.gaimediaplayer.services.seekProgres";
    Intent seekIntent;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        //create the service
        super.onCreate();
        videoService = this;
        videoMediaPlayer = new MediaPlayer();
        seekIntent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
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
            sendVideoState();
            handler.postDelayed(this, 1000);
        }
    };

    private void sendVideoState() {
        if (videoMediaPlayer.isPlaying()) {
            int mediaPosition = videoMediaPlayer.getCurrentPosition();
            int mediaMax = videoMediaPlayer.getDuration();
            seekIntent.putExtra("counter", String.valueOf(mediaPosition));
            seekIntent.putExtra("mediaMax", String.valueOf(mediaMax));
            seekIntent.putExtra("formattedTime", TimeFormatter.getTimeString(mediaPosition));
            sendBroadcast(seekIntent);
        }
    }

    public void setActivityParams(Activity activity, SurfaceView surfaceView, TextView textView, Video video, SurfaceHolder surfaceHolder) {
        this.fullScreenVideoActivity = activity;
        this.videoSurfaceView = surfaceView;
        this.videoNameTextView = textView;
        this.surfaceHolder = surfaceHolder;
        this.currentVideo = video;
    }

    private void analyzeIncomingVideo(Video video) {
        setMediaPlayerListeners();
        videoMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        videoMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.SCREEN_DIM_WAKE_LOCK);
        try {
            if (currentVideoUri != null && currentVideoUri.toString().equals(video.getUri().toString())) {
                // same video is coming in, maybe orientation just changed, continue playing video
                setVideoSurfaceView();
                // set name of video
                videoNameTextView.setText(video.getName());
                videoMediaPlayer.start();
            } else {
                // new video is being played...
                currentVideoUri = video.getUri();
                startVideo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVideo() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    videoMediaPlayer.setDataSource(getApplicationContext(), currentVideoUri);
                    videoMediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void setMediaPlayerListeners() {
        videoMediaPlayer.setOnPreparedListener(this);
        videoMediaPlayer.setOnCompletionListener(this);
        videoMediaPlayer.setOnErrorListener(this);
        videoMediaPlayer.setOnSeekCompleteListener(this);
    }

    private void setVideoSurfaceView() {
        int videoWidth = videoMediaPlayer.getVideoWidth();
        int videoHeight = videoMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = fullScreenVideoActivity.getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = fullScreenVideoActivity.getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = videoSurfaceView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoSurfaceView.setLayoutParams(lp);

        videoSurfaceView.setClickable(true);
    }

    public void play(Video currentPlayingVideo) {
        videoMediaPlayer.setDisplay(surfaceHolder);
        analyzeIncomingVideo(currentPlayingVideo);
    }

    public void pause() {
        videoMediaPlayer.pause();
    }

    public void stopVideo() {
        videoMediaPlayer.release();
        videoMediaPlayer = new MediaPlayer();
        this.currentVideoUri = null;
    }

    public MediaPlayer getVideoMediaPlayer() {
        return videoMediaPlayer;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setVideoSurfaceView();
        mediaPlayer.start();
        videoNameTextView.setText(currentVideo.getName());
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
