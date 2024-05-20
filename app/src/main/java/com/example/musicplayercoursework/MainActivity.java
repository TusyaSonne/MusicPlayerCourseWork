package com.example.musicplayercoursework;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayercoursework.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import adapter.CategoryAdapter;
import adapter.SectionSongListAdapter;
import models.CategoryModel;
import models.PlayListModel;
import models.SongModel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;

    private DrawerLayout drawerLayout;

    private static final int REQUEST_POST_NOTIFICATIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getCategories();
        setupSection("section_1", binding.section1MainLayout, binding.section1Title, binding.section1RecyclerView);
        setupSection("section_2", binding.section2MainLayout, binding.section2Title, binding.section2RecyclerView);
        setupSection("section_3", binding.section3MainLayout, binding.section3Title, binding.section3RecyclerView);
        mostlyPlayed("mostly_played", binding.mostlyPlayedMainLayout, binding.mostlyPlayedTitle, binding.mostlyPlayedRecyclerView);
        setupMyPlaylist(binding.myPlaylistMainLayout, binding.myPlaylistTitle, binding.myPlaylistRecyclerView);

        createNotificationChannel();

        handleOnBackPressed();


        //Бургер-меню

        Toolbar toolbar = binding.customToolbar;
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutAuthorFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        //Отображение почты пользователя в навигации
        TextView navHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            navHeaderEmail.setText(firebaseUser.getEmail());
        } else {
            navHeaderEmail.setText("Ваша почта");
        }

    }

    //уведомление о текущей песне
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            CharSequence name = "MusicChannel";
            String desctiption = "Channel for music notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("MUSIC_CHANNEL_ID", name, importance);
            channel.setDescription(desctiption);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showMusicNotification(String songTitle, String artistName, String coverUrl) {
        Glide.with(this)
                .asBitmap()
                .load(coverUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "MUSIC_CHANNEL_ID")
                                .setSmallIcon(R.drawable.logo) // Это будет резервная иконка, если загрузка изображения не удалась
                                .setLargeIcon(resource) // Устанавливаем загруженное изображение как крупную иконку
                                .setContentTitle(songTitle)
                                .setContentText(artistName)
                                .setPriority(NotificationCompat.PRIORITY_LOW)
                                .setOngoing(true)
                                .setOnlyAlertOnce(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // Запрос разрешения
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
                            return;
                        }
                        notificationManager.notify(1, builder.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // This method is required but can be left empty
                    }
                });
    }

    private void onSongChanged(String newSongTitle, String newArtistName) {
        showMusicNotification(newSongTitle, newArtistName, MyExoplayer.getCurrentSong().getCoverUrl());
    }

    private void stopMusicNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1); // Отменяет уведомление
    }



    //выбор фрагментов на бургер-меню
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Log.d("NAV_DRAWER", "MenuItem selected: " + id);

        if (id == R.id.home) {
            Log.d("NAV_DRAWER", "Returning to MainActivity content");
            // Удаляем все фрагменты и возвращаемся к основному содержимому MainActivity
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }
            }
        } else if (id == R.id.about_program) {
            Log.d("NAV_DRAWER", "Navigating to AboutProgramFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutProgramFragment()).commit();
        } else if (id == R.id.about_author) {
            Log.d("NAV_DRAWER", "Navigating to AboutAuthorFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutAuthorFragment()).commit();
        } else if (id == R.id.logout) {
            Log.d("NAV_DRAWER", "Performing logout");
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleOnBackPressed() {
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // If you want the default back behavior, call the super.onBackPressed() equivalent:
                    setEnabled(false);
                    onBackPressedDispatcher.onBackPressed();
                }
            }
        });
    }

//    void logout() {
//        if (MyExoplayer.getCurrentSong() == null) {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            stopMusicNotification();
//            finish();
//        }
//        else {
//            MyExoplayer.getInstance().release();
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            stopMusicNotification();
//            finish();
//            binding.playerView.setVisibility(View.GONE);
//        }
//    }

    void logout() {
        MyExoplayer.stopPlaying();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        showPlayerView();
        if (MyExoplayer.getCurrentSong() == null) {
            stopMusicNotification();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusicNotification(); // Уведомление отменяется при переходе в фоновый режим
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyExoplayer.getCurrentSong() != null) {
            MyExoplayer.getInstance().release();
        }
        stopMusicNotification(); // Уведомление отменяется при уничтожении активности
    }



    // Метод для отображения панели с текущей песней
    void showPlayerView() {

        binding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        if (MyExoplayer.getCurrentSong() != null) {
            onSongChanged(MyExoplayer.getCurrentSong().getTitle(), MyExoplayer.getCurrentSong().getSubtitle());
            binding.playerView.setVisibility(View.VISIBLE);
            binding.songTitleTextView.setText(MyExoplayer.getCurrentSong().getTitle());
            binding.songTitleTextView.setSelected(true);
            Glide.with(binding.songCoverImageView)
                    .load(MyExoplayer.getCurrentSong().getCoverUrl())
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(8))
                            .centerCrop()) // Скругление углов
                    .into(binding.songCoverImageView);
        } else {
            binding.playerView.setVisibility(View.GONE);
        }
    }

    void getCategories() {
        FirebaseFirestore.getInstance().collection("category")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = queryDocumentSnapshots.toObjects(CategoryModel.class);
                    setupCategoryRecyclerView(categoryList);
                });
    }

    private void setupCategoryRecyclerView(List<CategoryModel> categoryList) {
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
    }


    // Метод для установки секций
    void setupSection(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                        if (section != null) {
                            mainLayout.setVisibility(View.VISIBLE);
                            titleView.setText(section.getName());
                            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            SectionSongListAdapter adapter = new SectionSongListAdapter(section.getSongs());
                            recyclerView.setAdapter(adapter);
                            mainLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SongsListActivity.setCategory(section);
                                    Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }


    // Метод для установки чаще всего воспроизводимых песен
    void mostlyPlayed(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // Получение чаще всего воспроизводимых песен
                        FirebaseFirestore.getInstance().collection("songs")
                                .orderBy("count", Query.Direction.DESCENDING)
                                .limit(6)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot songListSnapshot) {
                                        List<SongModel> songModelList = songListSnapshot.toObjects(SongModel.class);
                                        List<String> songIdList = new ArrayList<>();
                                        for (SongModel songModel : songModelList) {
                                            songIdList.add(songModel.getId());
                                        }
                                        CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                                        if (section != null) {
                                            section.setSongs(songIdList);
                                            mainLayout.setVisibility(View.VISIBLE);
                                            titleView.setText(section.getName());
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                            recyclerView.setLayoutManager(layoutManager);
                                            SectionSongListAdapter adapter = new SectionSongListAdapter(section.getSongs());
                                            recyclerView.setAdapter(adapter);
                                            mainLayout.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SongsListActivity.setCategory(section);
                                                    Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                });

                    }
                });
    }

    //Метод для установки плейлиста пользователя
    void setupMyPlaylist(RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {

        boolean isMyPlaylistSelected = false;
        // Получить текущего пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Обработать ситуацию, когда пользователь не авторизован
            return;
        }

        String userId = currentUser.getUid();

        // Получить документ плейлиста пользователя из Firestore
        FirebaseFirestore.getInstance().collection("user_playlists").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                PlayListModel playlist = documentSnapshot.toObject(PlayListModel.class);
                if (playlist != null) {
                    // Создаем новый экземпляр класса CategoryModel из объекта класса PlayListModel
                    CategoryModel category = new CategoryModel();
                    category.setName("Мой плейлист");
                    category.setCoverUrl("https://firebasestorage.googleapis.com/v0/b/musicplayercoursework.appspot.com/o/section_images%2Fmy_playlist.jpg?alt=media&token=9f3afc28-2701-4c47-aba0-a36b3aa93291");
                    category.setSongs(playlist.getSongs());

                    // Передаем объект класса CategoryModel в метод setCategory() класса SongsListActivity
                    SongsListActivity.setCategory(category);
                    mainLayout.setVisibility(View.VISIBLE);
                    titleView.setText(category.getName());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    SectionSongListAdapter adapter = new SectionSongListAdapter(playlist.getSongs());
                    recyclerView.setAdapter(adapter);
                    mainLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SongsListActivity.setCategory(category);
                            Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }


}