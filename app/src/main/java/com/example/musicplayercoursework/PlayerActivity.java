package com.example.musicplayercoursework;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayercoursework.databinding.ActivityPlayerBinding;

import models.SongModel;

public class PlayerActivity extends AppCompatActivity {

    ActivityPlayerBinding binding;
    ExoPlayer exoPlayer;

    //Воспроизводить гифку только при воспроизведении трека
    Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Player.Listener.super.onIsPlayingChanged(isPlaying);
            showGif(isPlaying);
        }
    };


    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SongModel currentSong = MyExoplayer.getCurrentSong();
        if (currentSong != null) {
            binding.songTitleTextView.setText(currentSong.getTitle());
            binding.songSubtitleTextView.setText(currentSong.getSubtitle());
            Glide.with(binding.songCoverImageView)
                    .load(currentSong.getCoverUrl())
                    .circleCrop() //обрезка до круга
                    .into(binding.songCoverImageView);
            // Гифка проигрывания трека
            Glide.with(binding.songGifImageView)
                    .load(R.drawable.circle_playing1)
                    .circleCrop() //обрезка до круга
                    .into(binding.songGifImageView);
            // Установка проигрывателя
            exoPlayer = MyExoplayer.getInstance();
            binding.playerView.setPlayer(exoPlayer);
            //Установка проигрывателя видимым по умолчанию
            binding.playerView.showController();
            exoPlayer.addListener(playerListener);
        }

    }

    void showGif(Boolean show) {
        if (show) {
            binding.songGifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.songGifImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.removeListener(playerListener);
    }
}