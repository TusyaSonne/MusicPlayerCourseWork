package models;

import java.util.Objects;

public class SongModel {
    private String id;
    private String title;
    private String subtitle;
    private String url;
    private String coverUrl;

    public SongModel() {
        this.id = "";
        this.title = "";
        this.subtitle = "";
        this.url = "";
        this.coverUrl = "";
    }

    public SongModel(String id, String title, String subtitle, String url, String coverUrl) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.coverUrl = coverUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongModel songModel = (SongModel) o;
        return Objects.equals(id, songModel.id) && Objects.equals(title, songModel.title) && Objects.equals(subtitle, songModel.subtitle) && Objects.equals(url, songModel.url) && Objects.equals(coverUrl, songModel.coverUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, subtitle, url, coverUrl);
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", url='" + url + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
