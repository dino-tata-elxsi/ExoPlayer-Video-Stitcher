package com.dino.exoplayer_video_stitcher.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dino.exoplayer_video_stitcher.R;
import com.dino.exoplayer_video_stitcher.ad.AdType;
import com.dino.exoplayer_video_stitcher.utility.VideoConfig;
import com.dino.exoplayer_video_stitcher.video.PlayerManager;

public class VideoPlayerActivity extends AppCompatActivity {

    private PlayerManager playerManager;
    private AdType adType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve ad type from intent extras.
        String adTypeStr = getIntent().getStringExtra(VideoConfig.EXTRA_AD_TYPE);
        adType = AdType.valueOf(adTypeStr);

        // Initialize the exoplayer manager
        playerManager = new PlayerManager(this,findViewById(R.id.player_view));
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerManager.initializePlayers();
        playerManager.startPlayback(adType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        playerManager.resumePlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        playerManager.pausePlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerManager.releasePlayers();
    }

}