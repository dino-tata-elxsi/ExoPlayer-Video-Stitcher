# ExoPlayer-Video-Stitcher

A simple video player application demonstrating video playback and content stitching using ExoPlayer (Media3).

## Features

*   Uses ExoPlayer (Media3) for video playback.
*   Supports pre-roll, mid-roll, and post-roll ads.
*   Handles smooth transitions between ads and main content.
*   Preserves playback state across ad insertions.
*   Implements an ExoPlayerManager for better abstraction.
*   Supports error handling (fallback to the main video on ad error).
*   Basic controls: Play, Pause, Resume.

## Setup Instructions

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/dino-tata-elxsi/ExoPlayer-Video-Stitcher.git
    ```

2.  **Navigate to the Project Directory:**
    ```bash
    cd ExoPlayer-Video-Stitcher
    ```

3.  **Open in Android Studio:**
    *   Open Android Studio and select "Open an Existing Project."
    *   Navigate to the cloned project folder and select it.

4.  **Sync Gradle Files:**
    *   Ensure that Gradle dependencies are synced by clicking "Sync Now" in the toolbar.
    *   If prompted, install any missing dependencies.

5.  **Run the App:**
    *   Select an emulator or connected device.
    *   Click "Run" ▶ to build and install the app.

## Configuration

*   **Ad Insertion Intervals:** Modify `VideoConfig.java` to update ad insertion intervals.
*   **Video URLs:** Add or update video URLs in `VideoRepository.java`.

## Troubleshooting

*   **Gradle Sync Issues:** Try `File > Invalidate Caches & Restart`.
*   **Video Not Playing:** Ensure URLs in `VideoRepository.java` are valid.