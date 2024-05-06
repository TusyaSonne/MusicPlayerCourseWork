package com.example.musicplayercoursework;

import android.content.Context;
import android.media.browse.MediaBrowser;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Currency;
import java.util.Map;

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
            updateCount();
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

    public static void updateCount() {
        if (currentSong != null) {
            String id = currentSong.getId();
            FirebaseFirestore.getInstance().collection("songs")
                    .document(id)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Long latestCount = documentSnapshot.getLong("count");
                            if (latestCount == null) {
                                latestCount = 1L;
                            } else {
                                latestCount = latestCount+= 1;
                            }

                            FirebaseFirestore.getInstance().collection("songs")
                                    .document(id)
                                    .update("count", latestCount);
                        }
                    });
        }
    }
}
