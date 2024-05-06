package com.example.musicplayercoursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayercoursework.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
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
import models.SongModel;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPlayerView();
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
            binding.playerView.setVisibility(View.VISIBLE);
            binding.songTitleTextView.setText( "Сейчас играет " + MyExoplayer.getCurrentSong().getTitle());
            Glide.with(binding.songCoverImageView)
                    .load(MyExoplayer.getCurrentSong().getCoverUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(8))) // Скругление углов
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

}