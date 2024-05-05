package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayercoursework.SongsListActivity;
import com.example.musicplayercoursework.databinding.CategoryItemRecyclerRowBinding;

import java.util.List;

import models.CategoryModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private final List<CategoryModel> categoryList;

    public CategoryAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoryItemRecyclerRowBinding binding = CategoryItemRecyclerRowBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        holder.bindData(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CategoryItemRecyclerRowBinding binding;

        MyViewHolder(CategoryItemRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindData(CategoryModel category) {
            binding.nameTextView.setText(category.getName());
            // Использование библиотеки Glide для загрузки и отображения изображений
            Glide.with(binding.coverImageView)
                    .load(category.getCoverUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(32))) // Скругление углов
                    .into(binding.coverImageView);

            Log.i("SONGS", String.valueOf(category.getSongs().size()) + " " + category.getName()); //Логирование
            // Запуск SongsListActivity
            Context context = binding.getRoot().getContext();
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SongsListActivity.setCategory(category);
                    Intent intent = new Intent(context, SongsListActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }


}
