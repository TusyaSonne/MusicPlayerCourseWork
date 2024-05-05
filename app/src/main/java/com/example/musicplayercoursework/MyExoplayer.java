package com.example.musicplayercoursework;

import android.content.Context;
import android.media.browse.MediaBrowser;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.util.Currency;

import models.SongModel;

public class MyExoplayer {


    private static ExoPlayer exoPlayer = null;
    private static SongModel currentSong = null;

    public static SongModel getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        // Если нажали не на ту же самую песню - воспроизводим другую песню
        if (currentSong != song) {
            currentSong = song;
            if (currentSong != null && currentSong.getUrl() != null) {
                MediaItem mediaItem = MediaItem.fromUri(currentSong.getUrl());
                if (exoPlayer != null) {
                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.play();
                }
            }
        }

    }
}
