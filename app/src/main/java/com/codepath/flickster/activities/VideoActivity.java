package com.codepath.flickster.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.codepath.flickster.R;
import com.codepath.flickster.model.Video;
import com.codepath.flickster.network.NetworkManager;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = VideoActivity.class.getSimpleName();

    public static final String EXTRA_VIDEO_ID = TAG + ".EXTRA_VIDEO_ID" ;

    private NetworkManager networkManager;

    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;

    private  YouTubePlayer youTubePlayer;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        networkManager = NetworkManager.getNetworkManager();
        if(getIntent() != null && getIntent().hasExtra(EXTRA_VIDEO_ID)) {
            int id = getIntent().getIntExtra(EXTRA_VIDEO_ID, 0);
            if(id == 0) {
                // No ID passed
                Toast.makeText(VideoActivity.this, "No Video Found for this item", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                this.id = id;
                youTubePlayerView.initialize(getString(R.string.youtube_key),this);
            }
        }
    }

    /**
     * Handles the network connection to the Video API.
     *
     * @param id
     */
    private void fetchVideoInfo(int id) {
        networkManager.getVideoLink(id, new NetworkManager.VideoRequestCallback() {
            @Override
            public void onSuccess(ArrayList<Video> videoList) {
                if(videoList.size() > 0) {

                    try {
                        // Pass the first trailer to be played by youtube player.
                        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                            @Override
                            public void onFullscreen(boolean fullScreen) {
                                // Close the activity if user presses back or collapse button.
                                if (!fullScreen) {
                                    VideoActivity.this.finish();
                                }
                            }
                        });
                        youTubePlayer.setFullscreen(true);
                        youTubePlayer.loadVideo(videoList.get(0).getKey());
                    } catch (IllegalStateException ie) {
                        Toast.makeText(VideoActivity.this, "Error while playing the video. Please try again!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(VideoActivity.this, "Unable to get information from the API.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        fetchVideoInfo(this.id);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(VideoActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
