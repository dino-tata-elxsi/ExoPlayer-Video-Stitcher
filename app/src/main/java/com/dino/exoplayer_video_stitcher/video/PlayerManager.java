package com.dino.exoplayer_video_stitcher.video;

import static com.dino.exoplayer_video_stitcher.utility.CommonUtils.formatTime;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.dino.exoplayer_video_stitcher.ad.AdType;
import com.dino.exoplayer_video_stitcher.utility.VideoConfig;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private final Context context;
    private final PlayerView playerView;
    private ExoPlayer mainPlayer;
    private ExoPlayer adPlayer;
    private final VideoRepository videoRepository;
    private final Handler midrollHandler = new Handler(Looper.getMainLooper());
    private Runnable midrollRunnable;
    private static final String TAG = "PlayerManager";
    private long nextAdThreshold = VideoConfig.MID_ROLL_INSERTION_INTERVALS;

    public PlayerManager(Context context, PlayerView playerView) {
        this.context = context;
        this.playerView = playerView;
        this.videoRepository = new VideoRepository();
    }

    public void initializePlayers() {
        mainPlayer = new ExoPlayer.Builder(context).build();
        adPlayer = new ExoPlayer.Builder(context).build();
    }

    public void startPlayback(AdType adType) {
        switch (adType) {
            case PRE_ROLL:
                playPreRoll();
                break;
            case MID_ROLL:
                playMainWithMidRoll();
                break;
            case POST_ROLL:
                playMainThenPostRoll();
                break;
        }
    }

    //Play the ad first then the main video
    private void playPreRoll() {
        List<MediaItem> mediaItems = new ArrayList<>();
        MediaItem adMediaItem = MediaItem.fromUri(Uri.parse(videoRepository.getAdVideo().getVideoUrl()));
        MediaItem mainMediaItem = MediaItem.fromUri(Uri.parse(videoRepository.getMainVideo().getVideoUrl()));
        mediaItems.add(adMediaItem);
        mediaItems.add(mainMediaItem);
        playerView.setPlayer(mainPlayer);
        mainPlayer.setMediaItems(mediaItems);
        mainPlayer.prepare();
        mainPlayer.play();
    }

    //Start main video then insert the ad at Predefined intervals
    private void playMainWithMidRoll() {
        prepareMainContent(videoRepository.getMainVideo().getVideoUrl());

        //Add event listener
        setMainPlayerListener();
        playerView.setPlayer(mainPlayer);
        mainPlayer.play();

        // Initialize the next ad threshold (30 sec from start)
        nextAdThreshold = VideoConfig.MID_ROLL_INSERTION_INTERVALS;
        scheduleMidRollCheck();
        Log.i(TAG, "Main content is playing");
    }

    private void scheduleMidRollCheck() {
        // Remove any existing callbacks to prevent duplicate runnable.
        if (midrollRunnable != null) {
            midrollHandler.removeCallbacks(midrollRunnable);
        }

        // Define the runnable that checks the playback position.
        midrollRunnable = new Runnable() {
            @Override
            public void run() {
                // If the main player is not available, exit early.
                if (mainPlayer == null) {
                    return;
                }

                long currentPosition = mainPlayer.getCurrentPosition();

                // Check if the current playback position has reached or exceeded the threshold.
                if (currentPosition >= nextAdThreshold) {
                    handleMidRollAdInsertion(currentPosition);
                } else {
                    // Otherwise, post the runnable again after the configured interval.
                    midrollHandler.postDelayed(this, VideoConfig.MID_ROLL_CHECK_INTERVALS);
                }
            }
        };

        // Start the periodic checking.
        midrollHandler.postDelayed(midrollRunnable, VideoConfig.MID_ROLL_CHECK_INTERVALS);
    }

    // Handles the process of pausing the main content, playing the ad, and resuming the main content.
    private void handleMidRollAdInsertion(final long currentPosition) {
        Log.d(TAG, "Main content reached threshold: " + currentPosition);

        // Pause the main video and save the current position for resuming.
        mainPlayer.pause();
        Log.d(TAG, "Main content paused. resume position: " + currentPosition);

        // Play the ad. Once the ad completes, resume the main video and update the ad threshold.
        playAd(() -> {
            playMainContent(currentPosition);
            Log.i(TAG, "Ad finished; resumed main content at: " + currentPosition);
            nextAdThreshold += VideoConfig.MID_ROLL_INSERTION_INTERVALS;

            // Resume checking for the next mid-roll ad insertion.
            midrollHandler.postDelayed(midrollRunnable, VideoConfig.MID_ROLL_CHECK_INTERVALS);
        });
    }

    //Play main video and after completion, play the ad
    private void playMainThenPostRoll() {
        prepareMainContent(videoRepository.getMainVideo().getVideoUrl());

        //removing and adding listener for mainPlayer
        setMainPlayerListener();

        playerView.setPlayer(mainPlayer);
        mainPlayer.play();
        Log.d(TAG,"Playing main content");

        mainPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    mainPlayer.removeListener(this);
                    playAd(() -> {
                        Log.d(TAG,"ad playback complete");
                    });
                }
            }
        });
    }

    // Common method to play the ad and execute a callback when done.
    private void playAd(final AdCompletionCallback callback) {
        prepareAdContent(videoRepository.getAdVideo().getVideoUrl());

        playerView.setPlayer(adPlayer);
        adPlayer.play();
        Log.i(TAG, "Playing Ad content");

        adPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    Log.i(TAG, "Ad playback completed");
                    adPlayer.removeListener(this);
                    callback.onAdCompleted();
                }
            }
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.d(TAG,"Ad playback error: " + error);
                adPlayer.removeListener(this);
                //if the ad fails, continue with the main video.
                Log.i(TAG,"Ad playback failed and continue playing main video");
                callback.onAdCompleted();
            }
        });
    }

    //Resume or start main content at a specific position
    private void playMainContent(long startPosition) {
        if (mainPlayer == null) {
            prepareMainContent(videoRepository.getMainVideo().getVideoUrl());
        }

        //removing and adding listener for mainPlayer
        setMainPlayerListener();

        playerView.setPlayer(mainPlayer);
        if (startPosition > 0) {
            mainPlayer.seekTo(startPosition);
        }
        mainPlayer.play();
    }

    public void prepareMainContent(String url) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        mainPlayer.setMediaItem(mediaItem);
        mainPlayer.prepare();
    }

    public void prepareAdContent(String url) {
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        adPlayer.setMediaItem(mediaItem);
        adPlayer.prepare();
    }

    public void releasePlayers() {
        Log.d(TAG,"Releasing player");
        if (mainPlayer != null) {
            mainPlayer.release();
            mainPlayer = null;
        }
        if (adPlayer != null) {
            adPlayer.release();
            adPlayer = null;
        }
        if (midrollRunnable != null) {
            midrollHandler.removeCallbacks(midrollRunnable);
        }
    }

    public void  resumePlayback(){
        Log.d(TAG,"Playback resumed");
        if (playerView.getPlayer()!=null){
            playerView.getPlayer().play();
        }
    }

    public void pausePlayback(){
        Log.d(TAG,"Playback paused");
        if (playerView.getPlayer()!=null){
            playerView.getPlayer().pause();
        }
    }

    // Event handling for main video playback.
    private final Player.Listener mainPlayerListener = new Player.Listener() {
        @Override
        public void onPlayerError(PlaybackException error) {
            Log.e(TAG, "Main player error: " + error.getMessage());
            Toast.makeText(context.getApplicationContext(), "Facing issue with Player/Network",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onEvents(@NonNull Player player, Player.Events events) {
            if (events.contains(Player.EVENT_POSITION_DISCONTINUITY)){
                logPlaybackProgress(player);
            }
        }
    };

    public interface AdCompletionCallback {
        void onAdCompleted();
    }

    // Method to log playback progress
    private void logPlaybackProgress(Player player) {
        long currentPosition = player.getCurrentPosition();
        long duration = player.getDuration();
        Log.d(TAG, "Playback Progress: " + formatTime(currentPosition) +
                " / " + formatTime(duration));
    }

    private void setMainPlayerListener() {
        mainPlayer.removeListener(mainPlayerListener);
        mainPlayer.addListener(mainPlayerListener);
    }
}

