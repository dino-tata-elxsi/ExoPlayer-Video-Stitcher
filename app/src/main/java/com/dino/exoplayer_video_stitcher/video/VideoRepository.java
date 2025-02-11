package com.dino.exoplayer_video_stitcher.video;

import com.dino.exoplayer_video_stitcher.model.VideoContent;

import java.util.ArrayList;
import java.util.List;

public class VideoRepository {

    public List<VideoContent> getVideoContents() {
        List<VideoContent> videos = new ArrayList<>();
        videos.add(new VideoContent("https://storage.googleapis.com/gvabox/media/samples/stock.mp4"));
        videos.add(new VideoContent("https://www.w3schools.com/html/mov_bbb.mp4"));
        return videos;
    }


    /**
     * Returns the main video content.
     */
    public VideoContent getMainVideo() {
        List<VideoContent> videos = getVideoContents();
        return videos.get(0);
    }

    /**
     * Returns the ad video content.
     */
    public VideoContent getAdVideo() {
        List<VideoContent> videos = getVideoContents();
        return videos.size() > 1 ? videos.get(1) : null;
    }

}
