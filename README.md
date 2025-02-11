# ExoPlayer-Video-Stitcher
A simple video player application demonstrating video playback and content stitching using ExoPlayer.
Features
•	Uses ExoPlayer (Media3) for video playback.
•	Supports pre-roll, mid-roll, and post-roll ads.
•	Handles smooth transitions between ads and main content.
•	Preserves playback state across ad insertions.
•	Implements an ExoPlayerManager for better abstraction.
•	Supports error handling (fallback to main video on ad error).
•	Basic controls: Play, Pause, Resume.
Prerequisites
•	Android Studio (Latest Version)
•	Minimum SDK: 24
•	Target SDK: 35
•	Java & Kotlin Support
•	Internet Connection (For streaming videos)
Setup Instructions
1	Clone the Repository: git clone https://github.com/your-repo/exoplayer-video-stitcher.git
2	cd exoplayer-video-stitcher
3
4	Open in Android Studio:
◦	Open Android Studio and select "Open an Existing Project."
◦	Navigate to the cloned project folder and select it.
5	Sync Gradle Files:
◦	Ensure that Gradle dependencies are synced by clicking "Sync Now" in the toolbar.
◦	If prompted, install any missing dependencies.
6	Update Dependencies: Add the latest ExoPlayer dependency in build.gradle (app-level): dependencies {
7	    implementation 'androidx.media3:media3-exoplayer:1.2.0'
8	    implementation 'androidx.media3:media3-ui:1.2.0'
9	}
10
11	Run the App:
◦	Select an emulator or connected device.
◦	Click "Run" ▶ to build and install the app.
Configuration
•	Modify VideoConfig.java to update ad insertion intervals.
•	Add or update video URLs in VideoRepository.java.
•	Handle additional events in ExoPlayerManager.java if needed.
Troubleshooting
•	Gradle Sync Issues: Try File > Invalidate Caches & Restart.
•	Video Not Playing: Ensure URLs in VideoRepository.java are valid.