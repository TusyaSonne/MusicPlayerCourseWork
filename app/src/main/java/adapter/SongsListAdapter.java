package adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayercoursework.MyExoplayer;
import com.example.musicplayercoursework.PlayerActivity;
import com.example.musicplayercoursework.databinding.SongListItemRecyclerRowBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import models.SongModel;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.MyViewHolder> {

    private final List<String> songIdList;

    public SongsListAdapter(List<String> songIdList) {
        this.songIdList = songIdList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SongListItemRecyclerRowBinding binding = SongListItemRecyclerRowBinding.inflate(layoutInflater, parent, false);
//        SongListItemRecyclerRowBinding binding = SongListItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String songId = songIdList.get(position);
        holder.bindData(songId);
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SongListItemRecyclerRowBinding binding;

        public MyViewHolder(SongListItemRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindData(String songId) {
            FirebaseFirestore.getInstance().collection("songs")
                    .document(songId).get().addOnSuccessListener(documentSnapshot ->  {
                        SongModel song = documentSnapshot.toObject(SongModel.class);
                        binding.songTitleTextView.setText(song.getTitle());
                        binding.songSubtitleTextView.setText(song.getSubtitle());
                        // Использование библиотеки Glide для загрузки и отображения изображений
                        Glide.with(binding.songCoverImageView)
                                .load(song.getCoverUrl())
                                .apply(new RequestOptions().transform(new RoundedCorners(32))) // Скругление углов
                                .into(binding.songCoverImageView);
                        binding.getRoot().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyExoplayer.startPlaying(binding.getRoot().getContext(), song);
                                v.getContext().startActivity(new Intent(v.getContext(), PlayerActivity.class));
                            }
                        });
                    });
        }
    }
}
