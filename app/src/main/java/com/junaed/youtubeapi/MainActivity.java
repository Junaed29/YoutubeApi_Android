package com.junaed.youtubeapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private boolean fullScreen = false;
    private YouTubePlayer videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtubePlayerId);
        youTubeView.initialize(Config.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        if (!wasRestored) {
            videoPlayer = player;
            videoPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {

                @Override
                public void onFullscreen(boolean _isFullScreen) {
                    fullScreen = _isFullScreen;
                }
            });
            videoPlayer.loadVideo(getVideoId("https://youtu.be/upIarn54fX0"));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            // String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onBackPressed() {
        if (fullScreen){
            videoPlayer.setFullscreen(false);
        } else{
            super.onBackPressed();
        }
    }


    public static String getVideoId(String youtubeUrl) {
        String[] urlParts = youtubeUrl.split("\\?");

        if (urlParts[0].contains("youtu.be/")) {
            // Handle youtu.be URLs
            String[] pathParts = urlParts[0].split("/");
            return pathParts[pathParts.length - 1];
        } else if (urlParts.length > 1) {
            // Handle regular YouTube URLs
            String[] queryParts = urlParts[1].split("&");
            for (String query : queryParts) {
                String[] queryPair = query.split("=");
                if (queryPair.length == 2 && queryPair[0].equals("v")) {
                    return queryPair[1];
                }
            }
        }

        return null;
    }
}