package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryModel {
    private String name;
    private  String coverUrl;

    private List<String> songs;

    public CategoryModel() {
        this.name = "";
        this.coverUrl = "";
        this.songs = new ArrayList<>();
    }

    public CategoryModel(String name, String coverUrl, List<String> songs) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryModel that = (CategoryModel) o;
        return Objects.equals(name, that.name) && Objects.equals(coverUrl, that.coverUrl) && Objects.equals(songs, that.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coverUrl, songs);
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "name='" + name + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", songs=" + songs +
                '}';
    }
}
