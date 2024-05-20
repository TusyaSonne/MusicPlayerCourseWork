package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayListModel {
    private String id;
    private String userId;
    private List<String> songs;

    public PlayListModel() {
        this.id = "";
        this.userId = "";
        this.songs = new ArrayList<>();
    }

    public PlayListModel(String id, String userId, List<String> songs) {
        this.id = id;
        this.userId = userId;
        this.songs = songs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        PlayListModel that = (PlayListModel) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(songs, that.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, songs);
    }

    @Override
    public String toString() {
        return "PlayListModel{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", songs=" + songs +
                '}';
    }
}
