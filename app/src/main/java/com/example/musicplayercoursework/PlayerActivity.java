package com.example.musicplayercoursework;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import models.PlayListModel;
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
                    .load(R.drawable.circle_playing3)
                    .circleCrop() //обрезка до круга
                    .into(binding.songGifImageView);
            // Установка проигрывателя
            exoPlayer = MyExoplayer.getInstance();
            binding.playerView.setPlayer(exoPlayer);
            //Установка проигрывателя видимым по умолчанию
            binding.playerView.showController();
            exoPlayer.addListener(playerListener);

            // Проверяем, что песня уже добавлена в плейлист
            isSongInPlaylist(currentSong.getId());
        }

        binding.addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSongToPlaylist();
            }
        });

    }

    private void addSongToPlaylist() {
        // Получить текущего пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Обработать ситуацию, когда пользователь не авторизован
            return;
        }

        String userId = currentUser.getUid();

        // Получить идентификатор текущей песни
        String songId = MyExoplayer.getCurrentSong().getId();

        // Получить документ плейлиста пользователя из Firestore
        FirebaseFirestore.getInstance().collection("user_playlists").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PlayListModel playlist = documentSnapshot.toObject(PlayListModel.class);
                if (playlist == null) {
                    // Создать новый плейлист, если он еще не существует
                    playlist = new PlayListModel();
                    playlist.setUserId(userId);
                }

                // Добавить песню в плейлист
                playlist.getSongs().add(songId);

                // Сохранить обновленный плейлист в Firestore
                FirebaseFirestore.getInstance().collection("user_playlists").document(userId).set(playlist).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Песня успешно добавлена в плейлист", Toast.LENGTH_SHORT).show();
                        binding.addToPlaylistButton.setVisibility(View.GONE);
                        binding.playlistAddedIcon.setVisibility(View.VISIBLE);
                        binding.songAddedTextview.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private boolean isSongInPlaylist(String songId) {
        // Получить текущего пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        if (currentUser.isAnonymous()) {
            // Текущий пользователь не авторизован
            binding.addToPlaylistButton.setVisibility(View.GONE);
            binding.playlistAddedIcon.setVisibility(View.GONE);
            binding.songAddedTextview.setVisibility(View.GONE);
            return false;
        }

        String userId = currentUser.getUid();

        FirebaseFirestore.getInstance().collection("user_playlists").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PlayListModel playlist = documentSnapshot.toObject(PlayListModel.class);
                if (playlist != null && playlist.getSongs().contains(songId)) {
                    // Песня уже добавлена в плейлист
                    binding.addToPlaylistButton.setVisibility(View.GONE);
                    binding.playlistAddedIcon.setVisibility(View.VISIBLE);
                    binding.songAddedTextview.setVisibility(View.VISIBLE);
                } else {
                    // Песня не добавлена в плейлист
                    binding.addToPlaylistButton.setVisibility(View.VISIBLE);
                    binding.playlistAddedIcon.setVisibility(View.GONE);
                    binding.songAddedTextview.setVisibility(View.GONE);
                }
            }
        });

        // Возвращаем false по умолчанию, так как метод get() является асинхронным
        return false;
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