package com.dino.exoplayer_video_stitcher.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.dino.exoplayer_video_stitcher.databinding.ActivityHomeBinding;
import com.dino.exoplayer_video_stitcher.ad.AdType;
import com.dino.exoplayer_video_stitcher.utility.VideoConfig;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Added to support view in android devices targeting SDK 35 and above
        EdgeToEdge.enable(this);

        ActivityHomeBinding activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        //Button actions
        activityHomeBinding.preRollButton.setOnClickListener(view -> launchVideoPlayer(AdType.PRE_ROLL));
        activityHomeBinding.midRollButton.setOnClickListener(view -> launchVideoPlayer(AdType.MID_ROLL));
        activityHomeBinding.postRollButton.setOnClickListener(view -> launchVideoPlayer(AdType.POST_ROLL));
    }

    private void launchVideoPlayer(Enum adType) {
        //if adType is null  return
        if (adType == null){
            return;
        }
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoConfig.EXTRA_AD_TYPE, adType.name());
        startActivity(intent);
    }
}