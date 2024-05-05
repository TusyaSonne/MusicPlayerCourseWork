package com.example.musicplayercoursework;

import android.os.Bundle;
import android.view.View;

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
import com.example.musicplayercoursework.databinding.ActivitySongsListBinding;

import adapter.SongsListAdapter;
import models.CategoryModel;

public class SongsListActivity extends AppCompatActivity {

    public static CategoryModel category;

    private ActivitySongsListBinding binding;
    private SongsListAdapter songsListAdapter;

    public static CategoryModel getCategory() {
        return category;
    }

    public static void setCategory(CategoryModel category) {
        SongsListActivity.category = category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongsListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.nameTextView.setText(category.getName());
        Glide.with(binding.coverImageView)
                .load(category.getCoverUrl())
                .apply(new RequestOptions().transform(new RoundedCorners(32))) // Скругление углов
                .into(binding.coverImageView);

        setupSongsListRecyclerView();

    }

    void setupSongsListRecyclerView() {
        songsListAdapter = new SongsListAdapter(category.getSongs());
        binding.songsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.songsListRecyclerView.setAdapter(songsListAdapter);
    }

}